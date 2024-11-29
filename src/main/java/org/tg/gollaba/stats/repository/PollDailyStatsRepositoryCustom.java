package org.tg.gollaba.stats.repository;

import java.time.LocalDate;

public interface PollDailyStatsRepositoryCustom {

    void createAllDailyStats(LocalDate aggregationDate);
}