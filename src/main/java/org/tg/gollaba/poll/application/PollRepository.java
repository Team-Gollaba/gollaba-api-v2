package org.tg.gollaba.poll.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.poll.domain.Poll;

public interface PollRepository extends JpaRepository<Poll, Long> {
}
