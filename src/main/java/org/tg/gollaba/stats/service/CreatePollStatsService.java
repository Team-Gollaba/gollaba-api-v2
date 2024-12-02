package org.tg.gollaba.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.stats.repository.PollStatsRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreatePollStatsService {
    private final PollStatsRepository pollStatsRepository;

    @Transactional
    public void create(LocalDate aggregationDate) {
        pollStatsRepository.createAllStats(aggregationDate);
    }
}
