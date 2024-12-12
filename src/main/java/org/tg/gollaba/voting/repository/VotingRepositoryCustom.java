package org.tg.gollaba.voting.repository;

import java.util.List;
import java.util.Map;

public interface VotingRepositoryCustom {
    Map<Long, Map<Long, Integer>> votingCountMapByPollId(List<Long> pollIds);
    List<Long> findPollIdsByUserId(Long userId);
}
