package org.tg.gollaba.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.component.CookieHandler;
import org.tg.gollaba.auth.service.LoginByProviderTokenService;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.domain.User;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@RestController
@RequestMapping("/v2/auth/login/by-provider-token")
@RequiredArgsConstructor
public class LoginByProviderTokenController {
    private final LoginByProviderTokenService service;
    private final CookieHandler cookieHandler;
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @PostMapping
    ApiResponse<Response> login(@Valid @RequestBody Request request,
                            HttpServletRequest servletRequest,
                            HttpServletResponse servletResponse) {
        var issuedToken = service.login(request.providerToken(), request.providerType());

        addJwtRefreshTokenCookie(servletRequest, servletResponse, issuedToken);

        return ApiResponse.success(
            new Response(issuedToken.accessToken())
        );
    }

    private void addJwtRefreshTokenCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          IssuedToken issuedToken) {
        cookieHandler.deleteCookie(request, response, REFRESH_TOKEN);
        cookieHandler.addSecuredCookie(response, REFRESH_TOKEN, issuedToken.refreshToken(), (int) refreshExpirationTime / 1000);
    }

    record Request(
        @NotBlank(message = "providerToken 은 필수깂입니다")
        String providerToken,
        @NotNull(message = "providerType 은 필수값입니다")
        User.ProviderType providerType
    ) {
    }

    record Response(
        String accessToken
    ) {
    }
}
