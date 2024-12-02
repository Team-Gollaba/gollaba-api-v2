package org.tg.gollaba.auth.vo;

public record IssuedToken(
    String accessToken,
    String refreshToken
) {
}