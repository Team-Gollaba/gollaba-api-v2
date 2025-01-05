package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDate;
import java.util.List;

public interface PollRepositoryCustom {

    Page<PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Page<PollSummary> findMyPolls(Long userId, Pageable pageable);

    List<PollSummary> findTopPolls(LocalDate aggregationDate, int limit);

    List<PollSummary> findTrendingPolls(LocalDate aggregationDate, int limit);

    Page<PollSummary> findMyVotingPolls(Long userId, Pageable pageable);

    Page<PollSummary> findMyFavoritePolls(Long userId, Pageable pageable);
}
