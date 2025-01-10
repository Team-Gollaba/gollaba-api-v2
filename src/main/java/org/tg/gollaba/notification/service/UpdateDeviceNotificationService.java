package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UpdateDeviceNotificationService {
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void update(Requirement requirement) {
        var user = userRepository.findById(requirement.userId())
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));
        var deviceNotification = deviceNotificationRepository.findByUserIdAndAgentId(
                user.id(),
                requirement.agentId()
            )
            .orElseThrow(() -> new BadRequestException(Status.APP_NOTIFICATION_NOT_FOUND));

        deviceNotification.update(requirement.allowsNotification());
        deviceNotificationRepository.save(deviceNotification);
    }

    public record Requirement(
        Long userId,
        String agentId,
        boolean allowsNotification
    ) {
    }
}
