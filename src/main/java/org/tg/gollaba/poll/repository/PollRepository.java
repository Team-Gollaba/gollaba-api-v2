package org.tg.gollaba.poll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tg.gollaba.poll.domain.Poll;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long>, PollRepositoryCustom {
    @Query("SELECT p FROM Poll p WHERE p.id = :pollId AND p.userId = :userId")
    Optional<Poll> findMyPoll(@Param("pollId") Long pollId, @Param("userId") Long userId);

    @Query("SELECT pi.id FROM Poll p JOIN p.items pi WHERE p.id = :pollId")
    List<Long> findPollItemIdsByPollId(@Param("pollId") Long pollId);
}