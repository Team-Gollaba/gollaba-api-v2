package org.tg.gollaba.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tg.gollaba.notification.service.SendNotificationTerminatedPollService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    private final SendNotificationTerminatedPollService service;

    @Scheduled(
        cron = "0 */5 * * * *", // 5분마다
        zone = "Asia/Seoul"
    )
    public void terminatedPollList(){
        var from = LocalDateTime.now().minusMinutes(5).plusSeconds(1);
        var to = LocalDateTime.now();
        log.info("투표 종료 통계 스케쥴러 시작");
        service.notifyTerminatedPolls(from, to);
        log.info("투표 종료 통계 스케쥴러 종료");
    }
}
