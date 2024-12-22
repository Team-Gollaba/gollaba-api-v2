package org.tg.gollaba.voting.repository;

import java.util.List;
import java.util.Map;

public interface VotingRepositoryCustom {
    List<Map<String, Object>> getPollItemVotedNames(Long pollId, List<Long> pollItemIds);

}
