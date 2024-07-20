package org.tg.gollaba.stats.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tg.gollaba.stats.domain.PollStats;

import java.util.List;
import java.util.Optional;

public interface PollStatsRepository extends JpaRepository<PollStats, Long>, PollStatsRepositoryCustom {

//    List<PollStats> findAllByPollId(Iterable<Long> pollIds);

    Optional<PollStats> findByPollId(Long pollId);

    @Query("""
        SELECT ps.pollId
        FROM PollStats ps
        ORDER BY
            ps.totalVoteCount DESC,
            ps.totalReadCount DESC,
            ps.totalFavoritesCount DESC,
            ps.pollId DESC
    """)
    List<Long> findTopPollIds(Pageable pageable);

}
