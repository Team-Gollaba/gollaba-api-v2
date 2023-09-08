package org.tg.gollaba.vote.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.vote.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
