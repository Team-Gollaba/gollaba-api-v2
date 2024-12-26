package org.tg.gollaba.notification.service;

import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
//import org.tg.gollaba.app.domain.AppNotification;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.tg.gollaba.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CreateDeviceNotificationService {
    private final DeviceNotificationRepository appNotificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(Command command) {
            userRepository.findById(command.userId())
                .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));
        var existing = appNotificationRepository.findByUserIdAndAgentId(
            command.userId(),
            command.agentId()
        );
        if (existing.isPresent()){
            throw new BadRequestException(Status.DUPLICATE_DEVICE);
        }
       var appNotification = new DeviceNotification(
            command.userId(),
            command.agentId(),
            command.osType(),
            command.deviceName(),
            command.allowsNotification()
        );

        appNotificationRepository.save(appNotification);
    }

    public record Command(
        Long userId,
        String agentId,
        DeviceNotification.OperatingSystemType osType,
        String deviceName,
        boolean allowsNotification
    ) {
    }
}