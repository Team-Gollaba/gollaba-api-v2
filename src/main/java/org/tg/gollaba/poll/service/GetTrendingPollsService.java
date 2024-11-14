package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTrendingPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public List<PollSummary> get(int limit){
        var aggregationDate = java.time.LocalDate.now();
        return pollRepository.findTrendingPolls(aggregationDate, limit);
    }
}