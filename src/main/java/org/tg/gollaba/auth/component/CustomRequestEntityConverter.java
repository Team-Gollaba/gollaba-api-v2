package org.tg.gollaba.auth.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private final String APPLE_URL = "https://appleid.apple.com";

    @Value("${apple.team-id}")
    private String appleTeamId;
    @Value("${apple.key-id}")
    private String appleKeyId;
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;
    @Value("${spring.security.oauth2.client.registration.apple.client-secret}")
    private String applePrivateKey;

    private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

    public CustomRequestEntityConverter() {
        defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        var entity = defaultConverter.convert(req);
        var registrationId = req.getClientRegistration().getRegistrationId();
        var params = (MultiValueMap<String, String>) entity.getBody();

        if (registrationId.contains("apple")) {
            params.set("client_secret", generateClientSecret());
        }

        return new RequestEntity<>(params, entity.getHeaders(),
            entity.getMethod(), entity.getUrl());
    }

    private String generateClientSecret() {
        var now = Instant.now();

        // private key 디코딩 및 변환
        var privateKeyContent = applePrivateKey.replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        PrivateKey privateKey = null;
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return Jwts.builder()
            .setHeaderParam("kid", appleKeyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(appleTeamId)
            .setAudience("https://appleid.apple.com")
            .setSubject(appleClientId)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(300))) // 5분
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact();
    }
}
