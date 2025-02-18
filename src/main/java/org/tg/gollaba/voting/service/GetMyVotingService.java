package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.domain.VotingItem;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.tg.gollaba.common.support.Status.ALREADY_ANONYMOUS_VOTING;

@Service
@RequiredArgsConstructor
public class GetMyVotingService {
    private final VotingRepository votingRepository;
    private final DuplicatedVotingChecker duplicatedVotingChecker;

    @Transactional(readOnly = true)
    public VotingDetailVo getMyVoting(Long pollId, Long userId, String ipAddress) {
        checkDuplicatedVoting(pollId, userId, ipAddress);
        var voting = votingRepository.findActiveVotingBy(pollId, userId)
            .orElseThrow(() -> new BadRequestException(Status.VOTING_NOT_FOUND));
        var votedItemIds = voting.items().stream()
            .map(VotingItem::pollItemId)
            .collect(Collectors.toList());
        return new VotingDetailVo(
            voting.id(),
            votedItemIds
        );
    }

    private void checkDuplicatedVoting(Long pollId,
                                       Long userId,
                                       String ipAddress) {
        if(votingRepository.existsActiveVotingBy(pollId, userId)){
            return;
        }
        if(duplicatedVotingChecker.hasVoted(ipAddress, pollId)){
            throw new BadRequestException(ALREADY_ANONYMOUS_VOTING);
        }
    }

    public record VotingDetailVo(
        Long id,
        List<Long> votedItemIds
    ){}
}
