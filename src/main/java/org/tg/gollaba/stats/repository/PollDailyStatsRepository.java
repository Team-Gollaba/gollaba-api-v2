package org.tg.gollaba.stats.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tg.gollaba.stats.domain.PollDailyStats;

import java.time.LocalDate;
import java.util.List;

public interface PollDailyStatsRepository extends JpaRepository<PollDailyStats, Long>, PollDailyStatsRepositoryCustom {

    @Query("""
        SELECT pds
        FROM PollDailyStats pds
        WHERE pds.aggregationDate = :aggregationDate
        ORDER BY
            pds.voteCount DESC,
            pds.readCount DESC,
            pds.favoritesCount DESC,
            pds.pollId DESC
    """)
    List<PollDailyStats> findTrendingPolls(LocalDate aggregationDate, Pageable pageable);
    List<Long> findTrendingPollIds(LocalDate aggregationDate, int limit);
}