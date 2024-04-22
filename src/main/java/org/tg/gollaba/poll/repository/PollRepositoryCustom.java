package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;

public interface PollRepositoryCustom {

    Page<PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Page<PollSummary> findMyPolls(Long userId, Pageable pageable);

    List<PollSummary> findTopPolls(int limit);

    List<PollSummary> findTrendingPolls(int limit);

    Page<PollSummary> findMyVotingPolls(Long userId, Pageable pageable);
}
