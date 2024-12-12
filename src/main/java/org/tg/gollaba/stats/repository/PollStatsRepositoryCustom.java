package org.tg.gollaba.stats.repository;

import java.time.LocalDate;
import java.util.List;

public interface PollStatsRepositoryCustom {

    void createAllStats(LocalDate aggregationDate);
   List<Long> findTopPollIds(LocalDate aggregationDate, int limit);
}