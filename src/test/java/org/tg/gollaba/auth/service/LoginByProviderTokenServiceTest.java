package org.tg.gollaba.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.tg.gollaba.auth.OAuthClientRegistrationProvider;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.auth.vo.OAuthUserInfo;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LoginByProviderTokenServiceTest {

    @InjectMocks
    private LoginByProviderTokenService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private DefaultOAuth2UserService oAuth2UserService;
    @Mock
    private OAuthClientRegistrationProvider clientRegistrationProvider;

    @Test
    void login_ShouldReturnIssuedToken_WhenUserExists() {
        // given
        var accessToken = "dummyAccessToken";
        var providerType = User.ProviderType.GITHUB;
        var clientRegistration = mock(ClientRegistration.class);
        var oauth2User = testOAuth2User();
        var userInfo = OAuthUserInfo.github(oauth2User);
        var user = new UserFixture().build();

        given(clientRegistrationProvider.findByProviderType(providerType))
            .willReturn(clientRegistration);
        given(oAuth2UserService.loadUser(Mockito.any()))
            .willReturn(oauth2User);
        given(userRepository.findByProviderIdAndProviderType(userInfo.providerId(), providerType))
            .willReturn(Optional.of(user));
        given(tokenProvider.issue(user.id()))
            .willReturn(new IssuedToken("accessToken", "refreshToken"));

        // when
        var throwable = catchThrowable(() -> service.login(accessToken, providerType));

        // then
        assertThat(throwable).isNull();
    }

    @Test
    void login_ShouldThrowException_WhenUserDoesNotExist() {
        // given
        var accessToken = "dummyAccessToken";
        var providerType = User.ProviderType.GITHUB;
        var clientRegistration = mock(ClientRegistration.class);
        var oauth2User = testOAuth2User();
        var userInfo = OAuthUserInfo.github(oauth2User);

        given(clientRegistrationProvider.findByProviderType(providerType))
            .willReturn(clientRegistration);
        given(oAuth2UserService.loadUser(Mockito.any()))
            .willReturn(oauth2User);
        given(userRepository.findByProviderIdAndProviderType(userInfo.providerId(), providerType))
            .willReturn(Optional.empty());

        // when
        var throwable = catchThrowable(() -> service.login(accessToken, providerType));

        // then
        assertThat(throwable).isInstanceOf(BadRequestException.class);
        assertThat(throwable).hasFieldOrPropertyWithValue("status", Status.NOT_SIGN_UP);
    }

    private OAuth2User testOAuth2User() {
        return new OAuth2User() {
            @Override
            public Map<String, Object> getAttributes() {
                return Map.of(
                    "id", "dummyId",
                    "email", "dummyEmail",
                    "name", "dummyName",
                    "profile_image_url", "dummyProfileImageUrl"
                );
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }
}