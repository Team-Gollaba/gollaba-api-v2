package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator {
    static final String EMAIL_DUPLICATION_MESSAGE = "이미 존재하는 이메일입니다.";
    private final UserRepository userRepository;

    public void validate(User user) {
        checkDuplication(user.email());
    }

    private void checkDuplication(String email) {
        var user = userRepository.findByEmail(email);

        user.ifPresent(u -> {
            var providerType = u.providerType();
            throw new BadRequestException(
                Status.EMAIL_DUPLICATION,
                EMAIL_DUPLICATION_MESSAGE + " email: %s, providerType: %s"
                    .formatted(
                        email,
                        Optional.ofNullable(providerType)
                            .map(User.ProviderType::description)
                            .orElse("없음")
                    )
            );
        });
    }
}
