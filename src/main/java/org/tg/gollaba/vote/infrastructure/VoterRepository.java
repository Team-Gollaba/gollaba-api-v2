package org.tg.gollaba.vote.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.vote.domain.Voter;

public interface VoterRepository extends JpaRepository<Voter, Long> {
}
