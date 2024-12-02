package org.tg.gollaba.stats.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tg.gollaba.stats.service.CreatePollDailyStatsService;
import org.tg.gollaba.stats.service.CreatePollStatsService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsScheduler {
    private final CreatePollStatsService createPollStatsService;
    private final CreatePollDailyStatsService createPollDailyStatsService;

    @Scheduled(
        cron = "1 0 0 * * *", // 매일 0시 0분 1초에 실행
        zone = "Asia/Seoul"
    )
    public void createPollStats() {
        var aggregationDate = LocalDate.now();
        log.info("투표 통계 생성 스케줄러 시작");
        createPollStatsService.create(aggregationDate);
        createPollDailyStatsService.create(aggregationDate);
        log.info("투표 통계 생성 스케줄러 종료");
    }
}
