package org.tg.gollaba.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.stats.domain.PollSearchStats;

@Repository
public interface PollSearchStatsRepository extends JpaRepository<PollSearchStats, Long>, PollSearchStatsRepositoryCustom {
}
