package org.tg.gollaba.auth.component;

public interface TokenProvider {

    IssuedToken issue(Long userId);

    record IssuedToken(
        String accessToken,
        String refreshToken
    ) {
    }
}
