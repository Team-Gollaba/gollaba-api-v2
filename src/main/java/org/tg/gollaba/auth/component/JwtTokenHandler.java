package org.tg.gollaba.auth.component;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tg.gollaba.user.domain.User;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenHandler {

    private final String secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenHandler(@Value("${security.jwt.secret-key}") String secretKey,
                           @Value("${security.jwt.access-expiration-time}") long accessExpirationTime,
                           @Value("${security.jwt.refresh-expiration-time}") long refreshExpirationTime) {
        this.secretKey = secretKey;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public String createAccessToken(User user) {
        var now = new Date();
        var expirationTime = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
            .claim("uid", user.id())
            .setIssuer("gollaba")
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public String createRefreshToken() {
        var now = new Date();
        var expirationTime = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
            .setIssuer("gollaba")
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isValidToken(String token) {
        var result = false;

        try {
            parseToken(token);
            result = true;
        } catch (ExpiredJwtException ex) {
            log.warn("만료된 토큰입니다.", ex);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            log.error("잘못된 토큰입니다.", ex);
        } catch (Exception ex) {
            log.error("토큰 검증 중 오류가 발생했습니다.", ex);
        }

        return result;
    }
}
