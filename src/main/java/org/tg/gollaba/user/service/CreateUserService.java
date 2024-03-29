package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.user.component.UserValidator;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateUserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long create(Requirement requirement) {
        var user = createUserEntity(
            requirement
        );

        userValidator.validate(user);
        return userRepository.save(user).id();
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


    public record Requirement(
        String email,
        String name,
        Optional<String> password,
        Optional<String> profileImageUrl,
        Optional<User.ProviderType> providerType,
        Optional<String> providerId
    ) {
    }
}
