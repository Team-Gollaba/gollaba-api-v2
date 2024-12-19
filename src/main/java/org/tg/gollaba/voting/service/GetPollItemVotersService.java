package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetPollItemVotersService {
    private final PollRepository pollRepository;
    private final VotingRepository votingRepository;

    public List<Map<String, Object>> getVotedVoterNames(long pollId) {
        var poll =  pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));
        var pollItemIds = pollRepository.findPollItemIdsByPollId(poll.id());
        if(poll.pollType() != Poll.PollType.NAMED){
            throw new BadRequestException(Status.POLL_TYPE_NOT_NAMED);
        }

        return votingRepository.getPollItemVotedNames(pollId, pollItemIds);
    }
}
