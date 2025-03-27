package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.voting.component.VotingValidator;
import org.tg.gollaba.voting.domain.VoterName;
import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.domain.VotingItem;
import org.tg.gollaba.voting.listener.AuditVotingEventListener;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class UpdateVotingService {
    private final VotingRepository votingRepository;
    private final VotingValidator votingValidator;
    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void update(Requirement requirement) {
        var user = userRepository.findById(requirement.userId())
            .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        var voting = votingRepository.findById(requirement.votingId())
            .orElseThrow(() -> new BadRequestException(VOTING_NOT_FOUND));
        var poll = pollRepository.findById(voting.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        validate(user, poll, voting);
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
        eventPublisher.publishEvent(new AuditVotingEventListener.Event(voting));
    }

    private void validate(User user, Poll poll, Voting voting) {
        if (!Objects.equals(voting.userId(), user.id())) {
            throw new BadRequestException(VOTING_NOT_USER);
        }
        if (poll.endAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(VOTING_ALREADY_ENDED);
        }
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
        Long userId,
        Long votingId,
        Optional<String> voterName,
        Optional<Set<Long>> pollItemIds
    ) {
    }
}
