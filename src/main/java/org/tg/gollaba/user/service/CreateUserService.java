package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.OAuthClientRegistrationProvider;
import org.tg.gollaba.auth.vo.OAuthUserInfo;
import org.tg.gollaba.user.component.UserValidator;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateUserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuthClientRegistrationProvider clientRegistrationProvider;

    @Transactional
    public Long create(Requirement requirement) {
        var user = requirement.providerAccessToken()
            .map(providerAccessToken -> {
            var providerType =requirement.providerType().get();
            var clientRegistration = clientRegistrationProvider.findByProviderType(providerType);
            var getUserInfoRequest = createRequest(clientRegistration, providerAccessToken);
            var oAuthUserInfo = OAuthUserInfo.of(
                oAuth2UserService.loadUser(getUserInfoRequest),
                providerType
            );
            if(oAuthUserInfo.providerId().isEmpty()){
                throw new OAuthUserInfoLoadingException();
            }

            return createUserEntity(requirement, oAuthUserInfo);
            })
        .orElseGet(() -> createUserEntity(requirement));

        userValidator.validate(user);
        return userRepository.save(user).id();
    }

    static class OAuthUserInfoLoadingException extends IllegalArgumentException {
        public OAuthUserInfoLoadingException() {
            super("OAuth 사용자 정보를 로드하는 데 실패했습니다.");
        }
    }

    private OAuth2UserRequest createRequest(ClientRegistration clientRegistration,
                                              String accessToken) {
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

    private User createUserEntity(Requirement requirement) {
        return new User(
            requirement.email(),
            requirement.name(),
            passwordEncoder.encode(
                requirement.password()
                    .orElse(RandomStringUtils.randomAlphanumeric(7))
            ),
            requirement.profileImageUrl()
                .orElse(null),
            User.RoleType.USER,
            requirement.providerType().orElse(null),
            requirement.providerId().orElse(null)
        );
    }

    private User createUserEntity(Requirement requirement, OAuthUserInfo oAuthUserInfo) {
        return new User(
            requirement.email(),
            requirement.name(),
            passwordEncoder.encode(
                requirement.password()
                    .orElse(RandomStringUtils.randomAlphanumeric(7))
            ),
            requirement.profileImageUrl()
                .orElse(null),
            User.RoleType.USER,
            oAuthUserInfo.providerType(),
            oAuthUserInfo.providerId()
        );
    }


    public record Requirement(
        String email,
        String name,
        Optional<String> password,
        Optional<String> profileImageUrl,
        Optional<User.ProviderType> providerType,
        Optional<String> providerId,
        Optional<String> providerAccessToken
    ) {
    }
}
