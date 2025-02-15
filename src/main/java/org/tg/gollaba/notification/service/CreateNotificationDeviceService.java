package org.tg.gollaba.notification.service;

import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.repository.NotificationDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CreateNotificationDeviceService {
    private final NotificationDeviceRepository appNotificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(Requirement requirement) {
            userRepository.findById(requirement.userId())
                .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));
        var existing = appNotificationRepository.findByUserIdAndAgentIdAndDeletedAtIsNull(
            requirement.userId(),
            requirement.agentId()
        );
        if (existing.isPresent()){
            throw new BadRequestException(Status.DUPLICATE_DEVICE);
        }
       var appNotification = new NotificationDevice(
           requirement.userId(),
           requirement.agentId(),
           requirement.osType(),
           requirement.deviceName(),
           requirement.allowsNotification()
        );

        appNotificationRepository.save(appNotification);
    }

    public record Requirement(
        Long userId,
        String agentId,
        NotificationDevice.OperatingSystemType osType,
        String deviceName,
        boolean allowsNotification
    ) {
    }
}