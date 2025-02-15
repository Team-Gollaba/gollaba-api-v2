package org.tg.gollaba.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;

import static org.tg.gollaba.common.support.ExceptionUtils.ignoreException;

@Service
@RequiredArgsConstructor
public class ClosePollService {
    private final PollRepository pollRepository;
    private final AppNotificationSender appNotificationSender;

    @Transactional
    public void close(LocalDateTime from, LocalDateTime to) {
        pollRepository.findPollIdsByEndAtBetween(from, to)
            .forEach(pollId ->
                ignoreException(() -> appNotificationSender.pollClosed(pollId))
            );
    }
}