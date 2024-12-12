package org.tg.gollaba.voting.repository;

import java.util.List;
import java.util.Map;

public interface VotingItemRepositoryCustom {
    Map<Long, Integer> votingCountByPollItemIds(List<Long> pollItemIds);
}
