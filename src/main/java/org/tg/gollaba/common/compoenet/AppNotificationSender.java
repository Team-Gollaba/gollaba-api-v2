package org.tg.gollaba.common.compoenet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.client.FcmClient;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppNotificationSender {
    private final FcmClient fcmClient;
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;

    @Transactional
    public void sendServerNotice(NotificationMessage notificationMessage) {
        var deviceNotifications = deviceNotificationRepository.findAll()
            .stream()
            .filter(DeviceNotification::allowsNotification)
            .toList();
        if (deviceNotifications.isEmpty()) {
            log.info("No active notifications available to send server notice.");
            return;
        }

        deviceNotifications.forEach(userDevice -> {
            AppNotificationHistory history = null;
            var request = new FcmClient.Request(
                userDevice.agentId(),
                notificationMessage.title(),
                notificationMessage.content()
            );

            System.out.println("Sending FCM message with request: " + request);

            try {
                fcmClient.sendMessage(request);
                System.out.println("FCM message sent successfully.");
                history = createSuccessHistory(userDevice, AppNotificationHistory.Type.SERVER_NOTICE, request);
            } catch (Exception e) {
                System.out.println("Failed to send FCM message: " + e.getMessage());
                log.error("Failed to send FCM message", e);
                history = createFailureHistory(userDevice, AppNotificationHistory.Type.SERVER_NOTICE, e, request);
            } finally {
                appNotificationHistoryRepository.save(history);
                System.out.println("Notification history saved: " + history);
            }
        });
    }

    private AppNotificationHistory createSuccessHistory(DeviceNotification deviceNotification,
                                                        AppNotificationHistory.Type historyType,
                                                        FcmClient.Request request) {
        return createHistory(deviceNotification, historyType, AppNotificationHistory.Status.SUCCESS, null, request);
    }

    private AppNotificationHistory createFailureHistory(DeviceNotification deviceNotification,
                                                        AppNotificationHistory.Type historyType,
                                                        Exception e,
                                                        FcmClient.Request request) {
        return createHistory(deviceNotification, historyType, AppNotificationHistory.Status.FAILURE, e.getMessage(), request);
    }

    private AppNotificationHistory createHistory(DeviceNotification deviceNotification,
                                                 AppNotificationHistory.Type historyType,
                                                 AppNotificationHistory.Status historyStatus,
                                                 String failReason,
                                                 FcmClient.Request request) {
        return new AppNotificationHistory(
            historyType,
            historyStatus,
            deviceNotification.userId(),
            deviceNotification.agentId(),
            deviceNotification.id(),
            request.title(),
            request.content(),
            failReason
        );
    }
    public record NotificationMessage(
        String title,
        String content
    ){}
}
