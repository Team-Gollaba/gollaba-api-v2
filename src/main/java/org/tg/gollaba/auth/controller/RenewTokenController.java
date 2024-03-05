package org.tg.gollaba.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.component.CookieHandler;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.service.RenewTokenService;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.common.web.ApiResponse;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@RestController
@RequestMapping("/api/auth/renew-token")
@RequiredArgsConstructor
public class RenewTokenController {
    private final RenewTokenService renewTokenService;
    private final CookieHandler cookieHandler;
    @Value("${security.jwt.access-expiration-time}")
    private long accessExpirationTime;

    @PostMapping
    ApiResponse<Void> renew(@CookieValue(name = "refresh_token") String refreshToken,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        validate(refreshToken);
        var accessToken = renewTokenService.renew(refreshToken);

        addJwtTokenCookie(request, response, accessToken);
        return ApiResponse.success();
    }

    private void validate(String refreshToken) {
        if(StringUtils.isBlank(refreshToken)) {
            throw new BadRequestException(Status.INVALID_PARAMETER, "refresh token은 필수 입니다");
        }
    }

    private void addJwtTokenCookie(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String accessToken) {
        var cookieMaxAge = (int) accessExpirationTime / 1000;
        cookieHandler.deleteCookie(request, response, ACCESS_TOKEN);
        cookieHandler.addSecuredCookie(response, ACCESS_TOKEN, accessToken, cookieMaxAge);
    }
}
