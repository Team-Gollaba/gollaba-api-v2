package org.tg.gollaba.vote.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.vote.domain.Vote;
import org.tg.gollaba.vote.domain.VoteItem;
import org.tg.gollaba.vote.domain.VoteValidator;

import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.tg.gollaba.common.support.Status.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class VoteValidatorImpl implements VoteValidator {
    private final PollRepository pollRepository;

    public void validate(Vote vote) {
        var poll = getPoll(vote.pollId());

        checkPollEndTime(poll);
        validateVoteName(poll, vote.voterName());
        validateVoteItems(poll, vote.items());
    }

    private void checkPollEndTime(Poll poll) {
        if (now().isAfter(poll.endedAt())) {
            throw new VoteValidationException(POLL_IS_CLOSED);
        }
    }
    
    private void validateVoteName(Poll poll, String voterName) {
        if (poll.pollType() == Poll.PollType.ANONYMOUS
            && !voterName.startsWith(Vote.ANONYMOUS_NAME_PREFIX)) {
            throw new VoteValidationException(ANONYMOUS_NAME_REQUIRED);
        }
    }

    private void validateVoteItems(Poll poll, Set<VoteItem> items) {
        if (items.isEmpty()) {
            throw new VoteValidationException(INVALID_VOTE_ITEM_SIZE);
        }

        if (poll.responseType() == Poll.PollResponseType.SINGLE
            && items.size() > 1) {
            throw new VoteValidationException(INVALID_SINGLE_VOTE_ITEM_SIZE);
        }

        if (poll.responseType() == Poll.PollResponseType.MULTIPLE
            && poll.items().size() > items.size()) {
            throw new VoteValidationException(EXCEED_MULTIPLE_VOTE_ITEM_SIZE);
        }

        var pollItemIds = poll.items()
            .stream()
            .map(PollItem::id)
            .toList();
        var invalidItemIds = items.stream()
            .map(VoteItem::pollItemId)
            .filter(itemId -> !pollItemIds.contains(itemId))
            .collect(Collectors.toSet());

        if (!invalidItemIds.isEmpty()) {
            log.error("유효하지 않은 투표 항목 ID 에러 발생 // pollItemIds: {}", invalidItemIds);
            throw new VoteValidationException(INVALID_VOTE_ITEM);
        }
    }

    private Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
    }

    static class VoteValidationException extends BadRequestException {
        public VoteValidationException(Status status) {
            super(status);
        }
    }
}
