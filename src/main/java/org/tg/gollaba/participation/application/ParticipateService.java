package org.tg.gollaba.participation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.participation.domain.Participation;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.participation.domain.ParticipationItem;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class ParticipateService {
    private final PollRepository pollRepository;
    private final ParticipationRepository participationRepository;
    private final DuplicatedParticipationChecker duplicatedParticipationChecker;
    private final ParticipationValidator participationValidator;

    @Transactional
    public void participate(Requirement requirement) {
        checkAlreadyParticipation(requirement);

        var poll = pollRepository.findById(requirement.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        var participation = createParticipation(poll, requirement);

        participationValidator.validate(participation);
        participationRepository.save(participation);
        duplicatedParticipationChecker.record(requirement.ipAddress(), requirement.pollId());
    }

    private void checkAlreadyParticipation(Requirement requirement) {
        duplicatedParticipationChecker.check(requirement.ipAddress(), requirement.pollId());

        if (requirement.userId().isPresent()) {
            var userId = requirement.userId().get();
            var isAlreadyVote = participationRepository.existsByPollIdAndUserId(requirement.pollId(), userId);

            if (isAlreadyVote) {
                throw new BadRequestException(ALREADY_VOTED);
            }
        }
    }

    private Participation createParticipation(Poll poll,
                                              Requirement requirement) {
        var userId = requirement.userId()
            .orElse(null);
        var participantName = requirement.participantName()
            .orElse(null);
        var items = requirement.pollItemIds()
            .stream()
            .map(ParticipationItem::new)
            .collect(Collectors.toSet());

        return new Participation(
            poll,
            userId,
            participantName,
            items
        );
    }

    public record Requirement(
        Long pollId,
        Set<Long> pollItemIds,
        String ipAddress,
        Optional<Long> userId,
        Optional<String> participantName
    ) {
    }
}
