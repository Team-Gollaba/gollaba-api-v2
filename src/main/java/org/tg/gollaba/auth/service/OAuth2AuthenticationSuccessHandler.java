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
import org.tg.gollaba.auth.component.JwtTokenProvider;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.auth.vo.OAuthUserInfo;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.user.domain.User;

import java.io.IOException;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;
import static org.tg.gollaba.auth.repository.AuthorizationRequestRepositoryImpl.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final CookieHandler cookieHandler;
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

        if (!userRepository.existsByProviderIdAndProviderType(oAuth2UserInfo.providerId(), oAuth2UserInfo.providerType())) {
            var signUpUrl = generateSignupUrl(redirectUrl, oAuth2UserInfo);
            getRedirectStrategy().sendRedirect(request, response, signUpUrl);
            return;
        }

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + redirectUrl);
            return;
        }

        var user = userRepository.findByProviderIdAndProviderType(oAuth2UserInfo.providerId(), oAuth2UserInfo.providerType()).orElseThrow();
        var issuedToken = tokenProvider.issue(user.id());
        var targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("accessToken", issuedToken.accessToken())
            .queryParam("protectHash", "v")
            .build()
            .toUriString();

        clearAuthenticationAttributes(request, response);
        addJwtRefreshTokenCookie(request, response, issuedToken);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private OAuthUserInfo getOAuthUserInfo(Authentication authentication) {
        var authToken = (OAuth2AuthenticationToken) authentication;
        var providerType = User.ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        return switch (providerType) {
            case NAVER -> OAuthUserInfo.naver(authToken.getPrincipal());
            case KAKAO -> OAuthUserInfo.kakao(authToken.getPrincipal());
            case GITHUB -> OAuthUserInfo.github(authToken.getPrincipal());
            case APPLE -> OAuthUserInfo.apple(authToken.getPrincipal());
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

    private void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieHandler.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        cookieHandler.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        cookieHandler.deleteCookie(request, response, "JSESSIONID");
    }

    private void addJwtRefreshTokenCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          IssuedToken issuedToken) {
        cookieHandler.deleteCookie(request, response, REFRESH_TOKEN);
        cookieHandler.addSecuredCookie(response, REFRESH_TOKEN, issuedToken.refreshToken(), (int) refreshExpirationTime / 1000);
    }
}
