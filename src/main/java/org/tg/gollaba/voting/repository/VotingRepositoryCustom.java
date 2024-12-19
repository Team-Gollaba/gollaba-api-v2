package org.tg.gollaba.voting.repository;

import java.util.List;
import java.util.Map;

public interface VotingRepositoryCustom {
//    List<Map<Long, List<String>>> getPollItemVotedNames(Long pollId); //1차

    List<Map<String, Object>> getPollItemVotedNames(Long pollId); //2차

//    List<Map<String, Object>> getPollItemVotedNames(Long pollId, List<Long> pollItemIds); //TODO 빈 맵 반환으로 수정
}
