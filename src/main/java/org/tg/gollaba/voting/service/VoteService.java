package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.component.VotingValidator;
import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.domain.VoterName;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.voting.domain.VotingItem;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final PollRepository pollRepository;
    private final VotingRepository votingRepository;
    private final DuplicatedVotingChecker duplicatedVotingChecker;
    private final VotingValidator votingValidator;

    @Transactional
    public void vote(Requirement requirement) {
        checkAlreadyVoting(requirement);

        var poll = pollRepository.findById(requirement.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        var voting = createVoting(poll, requirement);

        votingValidator.validate(voting);
        votingRepository.save(voting);
        duplicatedVotingChecker.record(requirement.ipAddress(), requirement.pollId());
    }

    private void checkAlreadyVoting(Requirement requirement) {
        duplicatedVotingChecker.check(requirement.ipAddress(), requirement.pollId());

        if (requirement.userId().isPresent()) {
            var userId = requirement.userId().get();
            var isAlreadyVoting = votingRepository.existsActiveVotingBy(requirement.pollId(), userId);

            if (isAlreadyVoting) {
                throw new BadRequestException(ALREADY_VOTING);
            }
        }
    }

    private Voting createVoting(Poll poll, Requirement requirement) {
        var userId = requirement.userId()
            .orElse(null);
        var voterName = requirement.voterName()
            .map(name -> new VoterName(poll, name))
            .orElse(null);
        var items = requirement.pollItemIds()
            .stream()
            .map(VotingItem::new)
            .collect(Collectors.toSet());

        return new Voting(
            poll.id(),
            userId,
            voterName,
            items
        );
    }

    public record Requirement(
        Long pollId,
        Set<Long> pollItemIds,
        String ipAddress,
        Optional<Long> userId,
        Optional<String> voterName
    ) {
    }
}
