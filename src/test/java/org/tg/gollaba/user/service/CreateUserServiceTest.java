package org.tg.gollaba.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tg.gollaba.user.component.UserValidator;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void success() {
        //given
        var requirement = new CreateUserService.Requirement(
            "test@test.com",
            "test",
            Optional.empty(),
            Optional.of("https://www.test.com/test.jpg"),
            Optional.of(User.ProviderType.KAKAO),
            Optional.of("testProviderId")
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
}