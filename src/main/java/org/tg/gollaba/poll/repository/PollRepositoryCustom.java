package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;
import java.util.Map;

public interface PollRepositoryCustom {

    Page<PollSummary> findPollList(GetPollListService.Requirement requirement);

    GetPollDetailsService.PollDetails findPollDetails(Long id);

    Map<Long, Map<Long, Integer>> findPollItemIdsAndVoteCounts(List<Long> pollIds);
}
