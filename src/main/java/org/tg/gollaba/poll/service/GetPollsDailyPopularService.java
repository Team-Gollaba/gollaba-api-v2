package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.repository.PollDailyStatsRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetPollsDailyPopularService {
    private final PollDailyStatsRepository pollDailyStatsRepository;
    private final PollRepository pollRepository;

    @Transactional
    public Page<PollSummary> get(Pageable pageable){
        var pollStatsList = pollDailyStatsRepository
            .findTrendingPolls(LocalDate.now(), pageable);

        return pollRepository.getPollsDailyPopularSummary(pollStatsList, pageable);
    }
}
