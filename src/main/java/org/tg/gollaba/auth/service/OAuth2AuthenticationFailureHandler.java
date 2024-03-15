package org.tg.gollaba.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.tg.gollaba.auth.component.CookieHandler;

import java.io.IOException;

import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;
import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final CookieHandler cookieHandler;

   @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        var redirectUri = cookieHandler.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue)
            .orElse(("/"));

       log.error("oauth authentication fail", exception);

        var targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("error", exception.getLocalizedMessage())
            .build()
            .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        cookieHandler.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        cookieHandler.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
