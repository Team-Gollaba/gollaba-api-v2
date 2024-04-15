package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.domain.PollStats;
import org.tg.gollaba.stats.repository.PollStatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public List<PollSummary> get(int limit){
    return pollRepository.findTopPolls(limit);
    }
}
