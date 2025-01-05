package org.tg.gollaba.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.stats.domain.PollSearchStat;

@Repository
public interface PollSearchStatRepository extends JpaRepository<PollSearchStat, Long>, PollSearchStatRepositoryCustom {
}
