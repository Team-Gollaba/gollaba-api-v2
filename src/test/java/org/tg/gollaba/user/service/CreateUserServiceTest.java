package org.tg.gollaba.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.tg.gollaba.auth.OAuthClientRegistrationProvider;
import org.tg.gollaba.user.component.UserValidator;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {
    @InjectMocks
    private CreateUserService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;
    @Mock
    private OAuthClientRegistrationProvider clientRegistrationProvider;
    @Mock
    private DefaultOAuth2UserService oAuth2UserService;


    @Test
    void success() {
        //given
        var requirement = new CreateUserService.Requirement(
            "test@test.com",
            "test",
            Optional.empty(),
            Optional.of("https://www.test.com/test.jpg"),
            Optional.of(User.ProviderType.KAKAO),
            Optional.of("testProviderId"),
            Optional.empty()
        );
        given(passwordEncoder.encode(any()))
            .willReturn("testPassword");
        given(userRepository.save(any()))
            .willReturn(new UserFixture().build());

        //when
        var throwable = catchThrowable(() -> service.create(requirement));

        //then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).save(any(User.class));

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.email()).isEqualTo(requirement.email());
        assertThat(capturedUser.name()).isEqualTo(requirement.name());
        assertThat(capturedUser.password()).isEqualTo("testPassword");
        assertThat(capturedUser.profileImageUrl()).isEqualTo("https://www.test.com/test.jpg");
        assertThat(capturedUser.backgroundImageUrl()).isNull();
        assertThat(capturedUser.roleType()).isEqualTo(User.RoleType.USER);
        assertThat(capturedUser.providerType()).isEqualTo(User.ProviderType.KAKAO);
        assertThat(capturedUser.providerId()).isEqualTo("testProviderId");
    }

    @Test
    void ios_sign_up() {
        // given
        var requirement = new CreateUserService.Requirement(
            "test@test.com",
            "test",
            Optional.empty(),
            Optional.of("https://www.test.com/test.jpg"),
            Optional.of(User.ProviderType.KAKAO),
            Optional.empty(),
            Optional.of("testProviderAccessToken")
        );
        var oauth2User = testOAuth2User();
        var providerType = requirement.providerType().get();
        var clientRegistration = mock(ClientRegistration.class);

        given(passwordEncoder.encode(any()))
            .willReturn("testPassword");
        given(userRepository.save(any()))
            .willReturn(new UserFixture().build());
        given(clientRegistrationProvider.findByProviderType(providerType))
            .willReturn(clientRegistration);
        given(oAuth2UserService.loadUser(Mockito.any()))
            .willReturn(oauth2User);

        // when
        var throwable = catchThrowable(() -> service.create(requirement));

        // then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(clientRegistrationProvider, times(1))
            .findByProviderType(User.ProviderType.KAKAO);
        verify(oAuth2UserService, times(1)).loadUser(Mockito.any());

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.email()).isEqualTo(requirement.email());
        assertThat(capturedUser.name()).isEqualTo(requirement.name());
        assertThat(capturedUser.password()).isEqualTo("testPassword");
        assertThat(capturedUser.profileImageUrl()).isEqualTo("https://www.test.com/test.jpg");
        assertThat(capturedUser.backgroundImageUrl()).isNull();
        assertThat(capturedUser.roleType()).isEqualTo(User.RoleType.USER);
        assertThat(capturedUser.providerType()).isEqualTo(User.ProviderType.KAKAO);
        assertThat(capturedUser.providerId()).isEqualTo("testProviderId");
        assertThat(oAuth2UserService.loadUser(Mockito.any())).isNotNull();
    }

    private OAuth2User testOAuth2User() {
        var oauth2User = mock(OAuth2User.class);
        Map<String, Object> attributes = new HashMap<>();
        var kakaoAccount = new HashMap<>();
        var profile = new HashMap<>();

        profile.put("nickname", "testNickname");
        profile.put("profile_image_url", "https://www.test.com/test.jpg");

        kakaoAccount.put("email", "dummyEmail");
        kakaoAccount.put("profile", profile);

        attributes.put("id", "testProviderId");
        attributes.put("kakao_account", kakaoAccount);

        given(oauth2User.getAttributes()).willReturn(attributes);
        return oauth2User;
    }
}