package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.repository.VotingRepository;

import static org.tg.gollaba.common.support.Status.VOTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CancelVotingService {
    private final VotingRepository votingRepository;
    private final DuplicatedVotingChecker duplicatedVotingChecker;

    @Transactional
    public void cancel(Long votingId, String ipAddress) {
        var voting = votingRepository.findById(votingId)
            .orElseThrow(() -> new BadRequestException(VOTING_NOT_FOUND));

        voting.cancel();

        duplicatedVotingChecker.delete(ipAddress, voting.pollId());
        votingRepository.save(voting);
    }

}
