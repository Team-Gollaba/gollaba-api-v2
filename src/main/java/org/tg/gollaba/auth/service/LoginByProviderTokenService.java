package org.tg.gollaba.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.auth.vo.OAuthUserInfo;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.auth.OAuthClientRegistrationProvider;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoginByProviderTokenService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuthClientRegistrationProvider clientRegistrationProvider;

    public IssuedToken login(String providerAccessToken, User.ProviderType providerType) {
        var clientRegistration = clientRegistrationProvider.findByProviderType(providerType);
        var getUserInfoRequest = createRequest(clientRegistration, providerAccessToken);
        var oAuthUserInfo = OAuthUserInfo.of(
            oAuth2UserService.loadUser(getUserInfoRequest),
            providerType
        );

        return userRepository.findByProviderIdAndProviderType(oAuthUserInfo.providerId(), providerType)
            .map(user -> tokenProvider.issue(user.id()))
            .orElseThrow(() -> new BadRequestException(Status.NOT_SIGN_UP));
    }

    private OAuth2UserRequest createRequest(ClientRegistration clientRegistration, String accessToken) {
        return new OAuth2UserRequest(
            clientRegistration,
            new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessToken,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS)
            )
        );
    }
}
