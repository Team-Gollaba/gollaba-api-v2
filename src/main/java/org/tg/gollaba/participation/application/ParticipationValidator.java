package org.tg.gollaba.participation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.participation.domain.Participation;
import org.tg.gollaba.participation.domain.ParticipationItem;

import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.tg.gollaba.common.support.Status.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipationValidator {
    private final PollRepository pollRepository;

    public void validate(Participation participation) {
        var poll = getPoll(participation.poll().id());

        checkPollEndTime(poll);
        validateParticipantName(poll, participation.participantName());
        validateParticipationItems(poll, participation.items());
    }

    private void checkPollEndTime(Poll poll) {
        if (now().isAfter(poll.endedAt())) {
            throw new InvalidParticipationException(POLL_IS_CLOSED);
        }
    }
    
    private void validateParticipantName(Poll poll, String participantName) {
        if (poll.pollType() == Poll.PollType.ANONYMOUS
            && !participantName.startsWith(Participation.ANONYMOUS_NAME_PREFIX)) {
            throw new InvalidParticipationException(ANONYMOUS_NAME_REQUIRED);
        }
    }

    private void validateParticipationItems(Poll poll, Set<ParticipationItem> items) {
        if (items.isEmpty()) {
            throw new InvalidParticipationException(INVALID_PARTICIPATION_ITEM_SIZE);
        }

        if (poll.responseType() == Poll.PollResponseType.SINGLE
            && items.size() > 1) {
            throw new InvalidParticipationException(INVALID_SINGLE_PARTICIPATION_ITEM_SIZE);
        }

        if (poll.responseType() == Poll.PollResponseType.MULTIPLE
            && poll.items().size() > items.size()) {
            throw new InvalidParticipationException(EXCEED_MULTIPLE_PARTICIPATION_ITEM_SIZE);
        }

        var pollItemIds = poll.items()
            .stream()
            .map(PollItem::id)
            .toList();
        var invalidItemIds = items.stream()
            .map(ParticipationItem::pollItemId)
            .filter(itemId -> !pollItemIds.contains(itemId))
            .collect(Collectors.toSet());

        if (!invalidItemIds.isEmpty()) {
            log.error("유효하지 않은 투표 항목 ID 에러 발생 // pollItemIds: {}", invalidItemIds);
            throw new InvalidParticipationException(INVALID_PARTICIPATION_ITEM);
        }
    }

    private Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
    }

    static class InvalidParticipationException extends BadRequestException {
        public InvalidParticipationException(Status status) {
            super(status);
        }
    }
}
