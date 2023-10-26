package org.tg.gollaba.voting.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;

import static org.tg.gollaba.common.support.Status.VOTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CancelVotingService {
    private final VotingRepository votingRepository;

    @Transactional
    public void cancel(Long votingId) {
        var voting = votingRepository.findById(votingId)
            .orElseThrow(() -> new BadRequestException(VOTING_NOT_FOUND));

        voting.cancel();

        votingRepository.save(voting);
    }
}
