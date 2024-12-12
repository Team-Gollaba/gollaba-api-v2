package org.tg.gollaba.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.voting.domain.Voting;

public interface VotingItemRepository extends JpaRepository<Voting, Long>, VotingItemRepositoryCustom  {
}
