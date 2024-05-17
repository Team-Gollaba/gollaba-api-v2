package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.client.KakaoClient;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteUserService {
    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;

    @Transactional
    public void delete(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        kakaoClient.disconnect(user.providerId());

        userRepository.delete(user);
    }
}