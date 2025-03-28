package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.support.CachedList;
import org.tg.gollaba.config.CacheKeys;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheKeys.TOP_POLLS, key = "#limit")
    public List<PollSummary> get(int limit) {
        return pollRepository.findTopPolls(LocalDate.now(), limit);
    }
}