package org.tg.gollaba.poll.listner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.Optional;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CreatePollEventListener {
    private final GetPollListService getPollListService;

    @TransactionalEventListener
    public void handle(Event event) {
        refreshPollList();
    }

    private void refreshPollList() {
        IntStream.range(0, 3).forEach(i -> {
            var requirement = new GetPollListService.Requirement(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                PageRequest.of(i, 20),
                Optional.empty()
            );

            getPollListService.refresh(requirement);
        });
    }

    public record Event(
        Poll poll
    ) {
    }
}
