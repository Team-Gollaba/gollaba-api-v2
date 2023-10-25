package org.tg.gollaba.voting.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.voting.domain.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long> {

    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
