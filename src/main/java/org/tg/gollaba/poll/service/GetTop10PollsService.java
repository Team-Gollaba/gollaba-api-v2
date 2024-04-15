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

@Service
@RequiredArgsConstructor
public class GetTop10PollsService {
    private final PollRepository pollRepository;
    private final PollStatsRepository pollStatsRepository;

    @Transactional
    public Page<PollSummary> get(Pageable pageable){

    var pollStatsList = pollStatsRepository.findTopPolls(pageable);

    var pollIds = pollStatsList.stream()
        .map(PollStats::pollId)
        .toList();

    return pollRepository.getPollsSummary(pollIds, pageable);
    }
}
