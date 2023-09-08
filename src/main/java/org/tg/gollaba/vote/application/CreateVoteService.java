package org.tg.gollaba.vote.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.vote.domain.Vote;
import org.tg.gollaba.vote.domain.VoteItem;
import org.tg.gollaba.vote.domain.VoteValidator;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class CreateVoteService {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final VoteDuplicationChecker voteDuplicationChecker;
    private final VoteValidator voteValidator;

    @Transactional
    public void create(Requirement requirement) {
        checkAlreadyVote(requirement);

        var poll = pollRepository.findById(requirement.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));
        var voter = createVote(poll, requirement);

        voteRepository.save(voter);
        voteDuplicationChecker.save(requirement.ipAddress(), requirement.pollId());
    }

    private void checkAlreadyVote(Requirement requirement) {
        voteDuplicationChecker.check(requirement.ipAddress(), requirement.pollId());

        if (requirement.userId().isPresent()) {
            var userId = requirement.userId().get();
            var isAlreadyVote = voteRepository.existsByPollIdAndUserId(requirement.pollId(), userId);

            if (isAlreadyVote) {
                throw new BadRequestException(ALREADY_VOTED);
            }
        }
    }

    private Vote createVote(Poll poll,
                            Requirement requirement) {
        var userId = requirement.userId()
            .orElse(null);
        var voterName = requirement.voterName()
            .orElse(null);
        var items = requirement.pollItemIds()
            .stream()
            .map(VoteItem::new)
            .collect(Collectors.toSet());

        return new Vote(
            poll,
            userId,
            voterName,
            items,
            voteValidator
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
