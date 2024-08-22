package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.component.ProviderDisconnector;
import org.tg.gollaba.user.repository.UserRepository;

import static org.tg.gollaba.common.support.Status.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DeleteUserService {
    private final UserRepository userRepository;
    private final ProviderDisconnector providerDisconnector;

    @Transactional
    public void delete(long id) {
        userRepository.findById(id)
            .ifPresent(user -> {
                providerDisconnector.disconnect(user.providerType(), user.providerId());
                userRepository.delete(user);
            });
    }
}