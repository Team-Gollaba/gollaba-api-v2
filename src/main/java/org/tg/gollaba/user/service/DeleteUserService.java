package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.service.KakaoDisconnectService;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteUserService {
    private final KakaoDisconnectService kakaoDisconnectService;
    private final UserRepository userRepository;

    @Transactional
    public void delete(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));
        var providerId = Long.parseLong(user.providerId());

        kakaoDisconnectService.disconnect(providerId);

        userRepository.delete(user);
    }
}