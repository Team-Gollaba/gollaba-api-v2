package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UpdateUserService {
    private final UserRepository userRepository;

    @Transactional
    public void update(Requirement requirement) {
        var user = userRepository.findById(requirement.userId())
                .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        user.update(requirement.name());
        userRepository.save(user);
    }

    public record Requirement(
        Long userId,
        String name
    ){}
}
