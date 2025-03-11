package org.tg.gollaba.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.config.CacheKeys;
import org.tg.gollaba.stats.repository.PollDailyStatsRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreatePollDailyStatsService {
    private final PollDailyStatsRepository pollDailyStatsRepository;

    @Transactional
    @CacheEvict(value = CacheKeys.TRENDING_POLLS, allEntries = true)
    public void create(LocalDate aggregationDate) {
        pollDailyStatsRepository.createAllDailyStats(aggregationDate);
    }
}
