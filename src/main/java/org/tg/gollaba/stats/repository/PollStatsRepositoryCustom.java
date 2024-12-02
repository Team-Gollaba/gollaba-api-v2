package org.tg.gollaba.stats.repository;

import java.time.LocalDate;

public interface PollStatsRepositoryCustom {

    void createAllStats(LocalDate aggregationDate);
}