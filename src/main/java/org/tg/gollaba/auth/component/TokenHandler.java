package org.tg.gollaba.auth.component;

import io.jsonwebtoken.Claims;

public interface TokenHandler {

    String createAccessToken(Long userId);

    String createRefreshToken();

    Claims parseToken(String token);

    boolean isValidToken(String token);
}
