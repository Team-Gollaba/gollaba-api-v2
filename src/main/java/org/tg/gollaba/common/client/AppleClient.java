package org.tg.gollaba.common.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.common.support.JsonUtils;

import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AppleClient extends BaseHttpClient {
    private static final String BASE_URL = "https://appleid.apple.com";
    private final JwtTokenHandler jwtTokenHandler;
    @Value("${apple.team-id}")
    private String appleTeamId;
    @Value("${apple.key-id}")
    private String appleKeyId;
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;
    @Value("${spring.security.oauth2.client.registration.apple.client-secret}")
    private String applePrivateKey;

    public AppleClient(JwtTokenHandler jwtTokenHandler) {
        super(new RestTemplate());
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public AppleUserInfo getUserInfo(String code) {
        var uri = URI.create(BASE_URL + "/auth/token");
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var payload = new LinkedMultiValueMap<>();
        var clientSecret = generateClientSecret();

        payload.add("client_id", appleClientId);
        payload.add("client_secret", clientSecret);
        payload.add("code", code);
        payload.add("grant_type", "authorization_code");

        var requestEntity = new RequestEntity<>(payload, headers, HttpMethod.POST, uri);

        var response = exchange(
            requestEntity,
            GetUserInfoResponse.class,
            this::handleClientError,
            this::handleServerError
        );

        var idToken = response.idToken();
        var jwtParts = idToken.split("\\.");
        var jwtHeader = decodeJwtPart(jwtParts[0]);
        var jwtPayload = decodeJwtPart(jwtParts[1]);

        validateMatchedPublicKey(jwtHeader);

        return new AppleUserInfo(
            jwtPayload.get("sub").asText(),
            jwtPayload.get("email").asText(),
            Optional.ofNullable(jwtPayload.get("name"))
                .map(JsonNode::asText)
        );
    }

    private void validateMatchedPublicKey(JsonNode jwtHeader) {
        var isValid = getApplePublicKey().keys()
            .stream()
            .anyMatch(key -> key.kid().equals(jwtHeader.get("kid").asText()));

        if (!isValid) {
           throw new AppleClientException(401, "애플 로그인 인증 실패");
        }
    }

    private JsonNode decodeJwtPart(String encodedPart) {
        // URL-safe Base64 디코딩을 위해 패딩 처리
        String normalizedInput = encodedPart
            .replace('-', '+')
            .replace('_', '/');

        // 4의 배수가 되도록 패딩 추가
        while (normalizedInput.length() % 4 != 0) {
            normalizedInput += '=';
        }

        byte[] decodedBytes = Base64.getDecoder().decode(normalizedInput);
        return JsonUtils.readTree(new String(decodedBytes));
    }

    private ApplePublicKeyResponse getApplePublicKey() {
        var uri = URI.create(BASE_URL + "/auth/keys");
        var requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

        return exchange(
            requestEntity,
            ApplePublicKeyResponse.class,
            this::handleClientError,
            this::handleServerError
        );
    }

    private AppleClientException handleClientError(ClientHttpResponse clientHttpResponse) {
        var statusCode = extractResponseStatusCode(clientHttpResponse);
        var errorResponse = JsonUtils.parse(extractResponseBody(clientHttpResponse), ErrorResponse.class);
        return new AppleClientException(statusCode, errorResponse.error());
    }

    private AppleClientException handleServerError(ClientHttpResponse clientHttpResponse) {
        var statusCode = extractResponseStatusCode(clientHttpResponse);
        return new AppleClientException(statusCode);
    }

    private String generateClientSecret() {
        var now = Instant.now();

        return Jwts.builder()
            .setHeaderParam("kid", appleKeyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(appleTeamId)
            .setAudience("https://appleid.apple.com")
            .setSubject(appleClientId)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(300))) // 5분
            .signWith(SignatureAlgorithm.ES256, generatePrivateKey(applePrivateKey))
            .compact();
    }

    public static PrivateKey generatePrivateKey(String applePrivateKey) {
        // 1. 헤더와 푸터 제거, 공백과 줄바꿈 제거
        String privateKeyContent = applePrivateKey
            .replace("\\n", "\n")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", ""); // 모든 공백 제거

        // 2. Base64 디코딩
        byte[] privateKeyBytes;
        try {
            privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to decode private key. Ensure it is a valid Base64 string.", e);
        }

        // 3. PKCS8EncodedKeySpec을 사용해 KeySpec 생성
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        // 4. KeyFactory를 사용해 PrivateKey 객체 생성
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC"); // EC 알고리즘 (Apple 키에서 사용)
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm EC is not supported.", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key specification.", e);
        }
    }

    public record GetUserInfoResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        Long expiresIn,
        @JsonProperty("id_token")
        String idToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("token_type")
        String tokenType
    ) {
    }

    public record ErrorResponse(
        String error
    ) {
    }

    public record ApplePublicKeyResponse(
        @JsonProperty("keys")
        List<PublicKey> keys
    ) {
        public record PublicKey(
            @JsonProperty("kty")
            String kty,
            @JsonProperty("kid")
            String kid,
            @JsonProperty("alg")
            String alg,
            @JsonProperty("n")
            String n,
            @JsonProperty("e")
            String e
        ) {
        }
    }

    public record AppleUserInfo(
        String id,
        String email,
        Optional<String> name
    ) {
    }
}
