package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.Map;

public interface PollRepositoryCustom {

    Page<GetPollListService.PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Map<Long, Long> findVoteCounts(Long id);
}
