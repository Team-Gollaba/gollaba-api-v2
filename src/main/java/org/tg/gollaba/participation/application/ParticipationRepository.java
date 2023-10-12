package org.tg.gollaba.participation.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.participation.domain.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
