package org.tg.gollaba.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.user.domain.User;

import static org.tg.gollaba.common.support.Status.EMAIL_EXISTED;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validate(User user){
        var isEmail = userRepository.existsByEmail(user.email());

        if (isEmail){
            throw new BadRequestException(EMAIL_EXISTED);
        }
    }
}
