package org.tg.gollaba.voting.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.domain.VotingItem;

import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.tg.gollaba.common.support.Status.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class VotingValidator {
    private final PollRepository pollRepository;

    public void validate(Voting voting) {
        var poll = getPoll(voting.pollId());

        checkPollEndTime(poll);
        validateVotingItems(poll, voting.items());
    }

    private void checkPollEndTime(Poll poll) {
        if (now().isAfter(poll.endedAt())) {
            throw new InvalidVotingException(POLL_IS_CLOSED);
        }
    }

    private void validateVotingItems(Poll poll, Set<VotingItem> items) {
        if (items.isEmpty()) {
            throw new InvalidVotingException(INVALID_VOTING_ITEM_SIZE);
        }

        if (poll.responseType() == Poll.PollResponseType.SINGLE
            && items.size() > 1) {
            throw new InvalidVotingException(INVALID_SINGLE_VOTING_ITEM_SIZE);
        }

        var pollItemIds = poll.items()
            .stream()
            .map(PollItem::id)
            .toList();
        var invalidItemIds = items.stream()
            .map(VotingItem::pollItemId)
            .filter(itemId -> !pollItemIds.contains(itemId))
            .collect(Collectors.toSet());

        if (!invalidItemIds.isEmpty()) {
            log.error("유효하지 않은 투표 항목 ID 에러 발생 // pollItemIds: {}", invalidItemIds);
            throw new InvalidVotingException(INVALID_VOTING_ITEM);
        }
    }

    private Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
    }

    static class InvalidVotingException extends BadRequestException {
        public InvalidVotingException(Status status) {
            super(status);
        }
    }
}
