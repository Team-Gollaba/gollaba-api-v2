package org.tg.gollaba.voting.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.domain.VoterName;
import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.domain.VotingItem;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.VOTING_NOT_FOUND;
import static org.tg.gollaba.common.support.Status.POLL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UpdateVotingService {
    private final VotingRepository votingRepository;
    private final VotingValidator votingValidator;
    private final PollRepository pollRepository;

    @Transactional
    public void update(Requirement requirement) {
        var voting = votingRepository.findById(requirement.votingId())
            .orElseThrow(() -> new BadRequestException(VOTING_NOT_FOUND));
        var poll = pollRepository.findById(voting.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        var newVoterName = requirement.voterName()
            .map(name -> new VoterName(poll, name))
            .orElse(voting.voterName());
        var newItems = createNewVotingItem(
            voting,
            poll,
            requirement
        );

        voting.update(newVoterName, newItems);
        votingValidator.validate(voting);
        votingRepository.save(voting);
    }

    private Set<VotingItem> createNewVotingItem(Voting voting,
                                                Poll poll,
                                                Requirement requirement) {
        if (requirement.pollItemIds().isEmpty()) {
            return voting.items();
        }

        var ids = requirement.pollItemIds().get();

        return poll.getItems(ids)
            .stream()
            .map(PollItem::id)
            .map(VotingItem::new)
            .collect(Collectors.toSet());
    }

    public record Requirement(
        Long votingId,
        Optional<String> voterName,
        Optional<Set<Long>> pollItemIds
    ) {
    }
}
