package org.tg.gollaba.voting.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.voting.domain.Voting;

@Component
@RequiredArgsConstructor
public class AuditVotingEventListener {
    private final GetPollDetailsService getPollDetailsService;

    @TransactionalEventListener
    public void handle(Event event) {
        var voting = event.voting();

        getPollDetailsService.refresh(voting.pollId());
    }

    public record Event(
        Voting voting
    ) {
    }
}
