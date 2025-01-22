package org.tg.gollaba.auth.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class AppleOauthHandler {
    @Value("${apple.team-id}")
    private String appleTeamId;
    @Value("${apple.key-id}")
    private String appleKeyId;
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;
    @Value("${spring.security.oauth2.client.registration.apple.client-secret}")
    private String applePrivateKey;

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
