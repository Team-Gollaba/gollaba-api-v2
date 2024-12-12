package org.tg.gollaba.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.List;
import java.util.Map;

public interface PollRepositoryCustom {

    Page<Poll> findPollList(GetPollListService.Requirement requirement);
    Page<Poll> findMyPolls(Long userId, Pageable pageable);
//    Map<Long, Long> findUserIdsByPollIds(List<Long> pollIds);
    Map<Long, Long> findUserIdsByPollIds(List<Long> pollIds);
}
