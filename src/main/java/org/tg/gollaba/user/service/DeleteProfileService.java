package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteProfileService {
    private final UserRepository userRepository;

    @Transactional
    public void delete(Long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));
        user.deleteProfileImage();

        userRepository.save(user);
    }
}
