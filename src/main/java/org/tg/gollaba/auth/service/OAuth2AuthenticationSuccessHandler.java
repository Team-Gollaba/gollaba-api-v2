package org.tg.gollaba.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.tg.gollaba.auth.component.CookieHandler;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.vo.OAuthUserInfo;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.user.domain.User;

import java.io.IOException;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;
import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final CookieHandler cookieHandler;
    @Value("${security.jwt.access-expiration-time}")
    private long accessExpirationTime;
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var redirectUrl = cookieHandler.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());
        var oAuth2UserInfo = getOAuthUserInfo(authentication);

        if (!userRepository.existsByEmailAndProviderId(oAuth2UserInfo.email(), oAuth2UserInfo.providerId())) {
            var signUpUrl = generateSignupUrl(redirectUrl, oAuth2UserInfo);
            getRedirectStrategy().sendRedirect(request, response, signUpUrl);
            return;
        }

        var user = userRepository.findByEmailAndProviderId(oAuth2UserInfo.email(), oAuth2UserInfo.providerId()).orElseThrow();
        var issuedToken = tokenProvider.issue(user.id());

        addJwtTokenCookie(request, response, issuedToken);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + redirectUrl);
            return;
        }

        var targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("protectHash", "v")
            .build()
            .toUriString();
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private OAuthUserInfo getOAuthUserInfo(Authentication authentication) {
        var authToken = (OAuth2AuthenticationToken) authentication;
        var providerType = User.ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        return switch (providerType) {
            case NAVER -> OAuthUserInfo.naver(authToken.getPrincipal());
            case KAKAO -> OAuthUserInfo.kakao(authToken.getPrincipal());
        };
    }

    private String generateSignupUrl(String redirectUrl, OAuthUserInfo oAuthUserInfo) {
        var queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("name", oAuthUserInfo.name());
        queryParams.add("email", oAuthUserInfo.email());
        queryParams.add("providerId", oAuthUserInfo.providerId());
        queryParams.add("providerType", oAuthUserInfo.providerType().name());
        queryParams.add("profileImageUrl",
            oAuthUserInfo.profileImageUrl() != null
                ? oAuthUserInfo.profileImageUrl()
                : ""
        );
        queryParams.add("protectHash", "v");

        return UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParams(queryParams)
            .encode()
            .build()
            .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieHandler.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        cookieHandler.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        cookieHandler.deleteCookie(request, response, "JSESSIONID");
    }

    private void addJwtTokenCookie(HttpServletRequest request,
                                   HttpServletResponse response,
                                   TokenProvider.IssuedToken issuedToken) {
        cookieHandler.deleteCookie(request, response, ACCESS_TOKEN);
        cookieHandler.deleteCookie(request, response, REFRESH_TOKEN);
        cookieHandler.addSecuredCookie(response, ACCESS_TOKEN, issuedToken.accessToken(), (int) accessExpirationTime / 1000);
        cookieHandler.addSecuredCookie(response, REFRESH_TOKEN, issuedToken.refreshToken(), (int) refreshExpirationTime / 1000);
    }
}
