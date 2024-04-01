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
public class GetMyPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        var pollSummaries = pollRepository.findMyPolls(userId, pageable);

        return pollSummaries;
    }
}
