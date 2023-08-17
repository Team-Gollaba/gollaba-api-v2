package org.tg.gollaba.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tg.gollaba.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.domain.PollOption;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {

    @Query("select po from Poll p join fetch p.options po where po.id = :pollOptionId")
    Optional<PollOption> findByPollOptionId(@Param("pollOptionId") Long pollOptionId);
}
