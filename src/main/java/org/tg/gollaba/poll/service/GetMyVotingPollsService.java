package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;

@Service
@RequiredArgsConstructor
public class GetMyVotingPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        return pollRepository.findMyVotingPolls(userId, pageable);
    }
}
