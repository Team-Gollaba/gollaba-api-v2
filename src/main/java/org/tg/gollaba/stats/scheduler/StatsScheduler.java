package org.tg.gollaba.stats.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tg.gollaba.stats.service.CreatePollDailyStatsService;
import org.tg.gollaba.stats.service.CreatePollStatsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsScheduler {
    private final CreatePollStatsService createPollStatsService;
    private final CreatePollDailyStatsService createPollDailyStatsService;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24) // 매일
    public void createPollStats() {
        log.info("투표 통계 생성 스케줄러 시작");
        createPollStatsService.create();
        log.info("투표 통계 생성 스케줄러 종료");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24) // 매일
    public void createPollDailyStats() {
        log.info("투표 일일 통계 생성 스케줄러 시작");
        createPollDailyStatsService.create();
        log.info("투표 일일 통계 생성 스케줄러 종료");
    }
}
