package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.domain.PollDailyStats;

import java.util.List;

public interface PollRepositoryCustom {

    Page<PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Page<PollSummary> findMyPolls(Long userId, Pageable pageable);

    Page<PollSummary> getPollsSummary(List<Long> pollIds, Pageable pageable);

    Page<PollSummary> getPollsDailyPopularSummary(List<PollDailyStats> pollDailyStatsList, Pageable pageable);
}
