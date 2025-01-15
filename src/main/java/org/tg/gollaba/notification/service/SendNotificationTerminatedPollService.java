package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendNotificationTerminatedPollService {
    private final PollRepository pollRepository;
    private final AppNotificationSender appNotificationSender;

    @Transactional
    public void notifyTerminatedPolls(LocalDateTime from, LocalDateTime to) {
        var pollIds = pollRepository.findPollIdsByEndAtBetween(from, to);
        appNotificationSender.sendPollNotifications(pollIds);
    }
}