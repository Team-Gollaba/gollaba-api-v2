package org.tg.gollaba.common.compoenet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.client.FcmClient;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.NotificationDeviceRepository;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Objects;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toMap;
import static org.tg.gollaba.common.support.Status.POLL_NOT_FOUND;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppNotificationSender {
    private final FcmClient fcmClient;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;
    private final NotificationDeviceRepository notificationDeviceRepository;
    private final VotingRepository votingRepository;
    private final HashIdHandler hashIdHandler;
    private final PollRepository pollRepository;

    @Transactional
    public void pollClosed(long pollId) {
        var poll = pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        var votingList = votingRepository.findActiveByPollId(poll.id());
        var userIds = votingList.stream()
            .map(Voting::userId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        var devices = notificationDeviceRepository.findAllActiveByUserIds(userIds)
            .stream()
            .filter(NotificationDevice::allowsNotification)
            .toList();

        devices.forEach(device -> {
            var request = new FcmClient.Request(
                device.agentId(),
                "투표가 종료되었습니다.",
                "종료된 투표의 결과를 확인하세요.",
                "gollaba://voting?pollHashId=" + hashIdHandler.encode(pollId)
            );

            send(request, device, AppNotificationHistory.Type.POLL_CLOSED);
        });
    }

    private void send(FcmClient.Request request,
                      NotificationDevice notificationDevice,
                      AppNotificationHistory.Type notificationHistoryType) {
        AppNotificationHistory history = null;

        try {
            fcmClient.sendMessage(request);
            history = createSuccessHistory(notificationDevice, notificationHistoryType, request);
        } catch (FcmClient.AgentNotFoundException ae) {
            log.error("Failed to send FCM message", ae);
            notificationDevice.delete();
            notificationDeviceRepository.save(notificationDevice);
            history = createFailureHistory(notificationDevice, notificationHistoryType, request, ae);
        } catch (Exception e) {
            log.error("Failed to send FCM message", e);
            history = createFailureHistory(notificationDevice, notificationHistoryType, request, e);
        } finally {
            appNotificationHistoryRepository.save(history);
        }
    }

    private AppNotificationHistory createSuccessHistory(NotificationDevice notificationDevice,
                                                        AppNotificationHistory.Type historyType,
                                                        FcmClient.Request request) {
        return createHistory(notificationDevice, historyType, AppNotificationHistory.Status.SUCCESS, null, request);
    }

    private AppNotificationHistory createFailureHistory(NotificationDevice notificationDevice,
                                                        AppNotificationHistory.Type historyType,
                                                        FcmClient.Request request,
                                                        Exception e) {
        return createHistory(notificationDevice, historyType, AppNotificationHistory.Status.FAILURE, e.getMessage(), request);
    }

    private AppNotificationHistory createHistory(NotificationDevice notificationDevice,
                                                 AppNotificationHistory.Type historyType,
                                                 AppNotificationHistory.Status historyStatus,
                                                 String failReason,
                                                 FcmClient.Request request) {
        return new AppNotificationHistory(
            historyType,
            historyStatus,
            notificationDevice.userId(),
            notificationDevice.agentId(),
            notificationDevice.id(),
            request.title(),
            request.content(),
            failReason,
            request.deepLink()
        );
    }
}
