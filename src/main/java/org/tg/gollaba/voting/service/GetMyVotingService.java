package org.tg.gollaba.voting.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.repository.VotingRepository;
import org.tg.gollaba.voting.vo.VotingVo;

import static org.tg.gollaba.common.support.Status.VOTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GetMyVotingService {
    private final VotingRepository votingRepository;

    @Transactional(readOnly = true)
    public VotingVo getMyVoting(Long pollId, Long userId) {
        var voting = votingRepository.findActiveVotingBy(pollId, userId)
            .orElseThrow(() -> new BadRequestException(VOTING_NOT_FOUND));

        return VotingVo.from(voting);
    }
}
