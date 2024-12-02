package org.tg.gollaba.user.component;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.tg.gollaba.user.component.UserValidator.EMAIL_DUPLICATION_MESSAGE;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @InjectMocks
    private UserValidator validator;
    @Mock
    private UserRepository userRepository;

    @DisplayName("이메일 중복 검사")
    @Test
    void checkEmailDuplication() {
        //given
        var user = new UserFixture().build();
        given(userRepository.findByEmail(user.email()))
            .willReturn(Optional.of(user));

        //when
        var throwable = catchThrowable(() -> validator.validate(user));

        //then
        Assertions.assertThat(throwable)
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining(EMAIL_DUPLICATION_MESSAGE);
    }
}