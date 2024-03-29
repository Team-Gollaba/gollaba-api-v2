package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;

public interface PollRepositoryCustom {

    Page<GetPollListService.PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);
}
