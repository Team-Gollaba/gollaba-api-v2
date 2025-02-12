package org.tg.gollaba.common.compoenet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.client.FcmClient;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppNotificationSender {
    private static final AppNotificationHistory.Type TYPE = AppNotificationHistory.Type.POLL_TERMINATE;
    private static final String TITLE = "투표가 종료되었습니다.";
    private static final String CONTENT = "종료된 투표의 결과를 확인하세요.";

    private final FcmClient fcmClient;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final HashIdHandler hashIdHandler;

    @Transactional
    public void sendPollNotifications(List<Long> pollIds) {
        var targetPollIds = pollIds.stream()
            .filter(Objects::nonNull)
            .toList();
        var targetDevices = deviceNotificationRepository.findNotiAllowUsers(targetPollIds);
        var deviceMap = targetDevices.stream() //pollId:targetDevice
            .collect(toMap(DeviceNotification::userId, device -> device));

        send(targetPollIds, deviceMap);
    }

    private void send(List<Long> targetPollIds, Map<Long, DeviceNotification> deviceMap){
        targetPollIds.stream()
            .filter(deviceMap::containsKey)
            .forEach(pollId -> {
                var targetDevice = deviceMap.get(pollId);

            AppNotificationHistory history = null;
            var request = new FcmClient.Request(
                targetDevice.agentId(),
                TITLE,
                CONTENT
            );

            try {
                fcmClient.sendMessage(request);
                history = createSuccessHistory(targetDevice, TYPE, request, pollId.toString());
            } catch (Exception e) {
                log.error("Failed to send FCM message", e);
                history = createFailureHistory(targetDevice, TYPE, e, request);
            } finally {
                appNotificationHistoryRepository.save(history);
            }
        });
    }

    private AppNotificationHistory createSuccessHistory(DeviceNotification deviceNotification,
                                                        AppNotificationHistory.Type historyType,
                                                        FcmClient.Request request,
                                                        String eventId) {
        return createHistory(deviceNotification, historyType, AppNotificationHistory.Status.SUCCESS, null, request, eventId);
    }

    private AppNotificationHistory createFailureHistory(DeviceNotification deviceNotification,
                                                        AppNotificationHistory.Type historyType,
                                                        Exception e,
                                                        FcmClient.Request request) {
        return createHistory(deviceNotification, historyType, AppNotificationHistory.Status.FAILURE, e.getMessage(), request, null);
    }

    private AppNotificationHistory createHistory(DeviceNotification deviceNotification,
                                                 AppNotificationHistory.Type historyType,
                                                 AppNotificationHistory.Status historyStatus,
                                                 String failReason,
                                                 FcmClient.Request request,
                                                 String eventId) {
        var pollId = parseLong(eventId);
        var pollHashId = hashIdHandler.encode(pollId);
        return new AppNotificationHistory(
            historyType,
            historyStatus,
            deviceNotification.userId(),
            deviceNotification.agentId(),
            deviceNotification.id(),
            request.title(),
            request.content(),
            failReason,
            eventId,
            "Gollaba-app//notification?pollHashId=" + pollHashId
        );
    }
}
