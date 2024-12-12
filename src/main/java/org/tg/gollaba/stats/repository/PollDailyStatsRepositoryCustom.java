package org.tg.gollaba.stats.repository;

import java.time.LocalDate;
import java.util.List;

public interface PollDailyStatsRepositoryCustom {

    void createAllDailyStats(LocalDate aggregationDate);
    List<Long> findTrendingPollIds(LocalDate aggregationDate, int limit);
}