package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    static final String EMAIL_DUPLICATION_MESSAGE = "이미 존재하는 이메일입니다.";
    private final UserRepository userRepository;

    public void validate(User user) {
        checkDuplication(user.email(), user.providerId());
    }

    private void checkDuplication(String email,
                                  String providerId) {
        if (userRepository.existsByEmailAndProviderId(email, providerId)) {
            throw new BadRequestException(
                Status.INVALID_PARAMETER,
                EMAIL_DUPLICATION_MESSAGE
            );
        }
    }
}
