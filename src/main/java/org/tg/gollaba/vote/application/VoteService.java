package org.tg.gollaba.vote.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.repository.PollRepository;
import org.tg.gollaba.vote.domain.Voter;
import org.tg.gollaba.vote.infrastructure.VoterRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final PollRepository pollRepository;
    private final VoterRepository voterRepository;

    @Transactional
    public void vote(Requirement requirement) {
        var pollItem = pollRepository.findByPollOptionId(requirement.pollOptionId())
            .orElseThrow(() -> new BadRequestException(Status.POLL_ITEM_NOT_FOUND));
        var voter = createVoter(pollItem, requirement);

        voterRepository.save(voter);
    }

    private Voter createVoter(PollOption polItem,
                              Requirement requirement) {
        return new Voter(
            polItem.getPoll().getId(),
            requirement.userId()
                .orElse(null),
            requirement.voterName()
                .orElse(null),
            polItem
        );
    }

    public record Requirement(
        Long pollOptionId,
        Optional<Long> userId,
        Optional<String> voterName
    ) {
    }
}
