package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

public interface PollRepositoryCustom {

    Page<PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Page<PollSummary> findPollItemIdsAndVoteCounts(Long userId, Pageable pageable);
}
