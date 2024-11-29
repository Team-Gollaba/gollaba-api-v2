package org.tg.gollaba.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.stats.repository.PollDailyStatsRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreatePollDailyStatsService {
    private final PollDailyStatsRepository pollDailyStatsRepository;

    @Transactional
    public void create(LocalDate aggregationDate) {
        pollDailyStatsRepository.createAllDailyStats(aggregationDate);
    }
}
