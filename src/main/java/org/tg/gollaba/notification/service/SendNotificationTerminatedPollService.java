package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.common.compoenet.AppNotificationSender.NotificationMessage;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class SendNotificationTerminatedPollService {
    private final PollRepository pollRepository;
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final AppNotificationSender appNotificationSender;
    private final AppNotificationHistoryRepository appNotificationHistoryRepository;

    @Transactional
    public void send(LocalDate aggregationDate) {
        var polls = pollRepository.findTerminatedPollsBefore(aggregationDate);

        var excludeUsers = appNotificationHistoryRepository.findExcludeUsers(polls);

        var targetDevices = deviceNotificationRepository.findNotiAllowUsers(

            polls.stream()
                .map(Poll::userId)
                .filter(Objects::nonNull)
                .filter(userId -> !excludeUsers.contains(userId))
                .toList()
        );

        var userPollMap = polls.stream()
            .filter(poll -> targetDevices.stream()
                .anyMatch(device -> device.userId().equals(poll.userId())))
            .collect(toMap(
                Poll::userId,
                Poll::id
            ));

        appNotificationSender.send(
            new NotificationMessage(
                targetDevices,
                userPollMap,
                AppNotificationHistory.Type.POLL_TERMINATE,
                "투표가 종료되었습니다.",
                "종료된 투표의 결과를 확인하세요."
            )
        );
    }
}