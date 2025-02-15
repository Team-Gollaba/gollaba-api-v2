package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.NotificationDeviceRepository;
import org.tg.gollaba.notification.vo.AppNotificationVo;

@Service
@RequiredArgsConstructor
public class GetUserNotificationListService {
    private final NotificationDeviceRepository notificationDeviceRepository;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;

    @Transactional(readOnly = true)
    public Page<AppNotificationVo> get(Long userId, Pageable pageable) {
        var deviceNotifications = notificationDeviceRepository.findAllActiveByUserId(userId);
        var userAgentIds = deviceNotifications.stream()
            .map(NotificationDevice::agentId)
            .toList();
        var appNotificationHistories = appNotificationHistoryRepository.findUserNotifications(
            userId,
            userAgentIds,
            pageable
        );

        return appNotificationHistories.map(notification -> new AppNotificationVo(
            notification.id(),
            notification.userId(),
            notification.deepLink(),
            notification.title(),
            notification.content()
        ));
    }
}
