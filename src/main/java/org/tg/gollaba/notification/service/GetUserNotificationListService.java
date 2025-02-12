package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;
import org.tg.gollaba.notification.vo.AppNotificationVo;

@Service
@RequiredArgsConstructor
public class GetUserNotificationListService {
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;

    @Transactional(readOnly = true)
    public Page<AppNotificationVo> get(Long userId, Pageable pageable) {
        var deviceNotifications = deviceNotificationRepository.findByUserId(userId);
        var userAgentIds = deviceNotifications.stream()
            .map(DeviceNotification::agentId)
            .toList();
        var appNotificationHistories = appNotificationHistoryRepository.findUserNotifications(
            userId,
            userAgentIds,
            pageable
        );

        return appNotificationHistories.map(notification -> new AppNotificationVo(
            notification.id(),
            notification.userId(),
            notification.agentId(),
            notification.eventId(),
            notification.deepLink(),
            notification.title(),
            notification.content()
        ));
    }
}
