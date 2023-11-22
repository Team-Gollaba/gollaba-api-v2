package org.tg.gollaba.user.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.user.domain.UserFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    UserValidator validator;

    @Mock
    UserRepository userRepository;

    @DisplayName("중복된 이메일로 가입할 경우 예외가 발생한다.")
    @Test
    void existedEmail() {
        //given
        var userFixture = new UserFixture();
        var signedUser = userFixture.build();
        given(userRepository.existsByEmail(signedUser.email()))
            .willReturn(true);
        var newUser = new UserFixture()
            .setEmail(signedUser.email())
            .build();

        //when
        var throwable = catchThrowable(() -> validator.validate(newUser));

        //then
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }
}