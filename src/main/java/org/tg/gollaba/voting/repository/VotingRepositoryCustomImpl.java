package org.tg.gollaba.voting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import static org.tg.gollaba.voting.domain.QVoting.voting;
import static org.tg.gollaba.voting.domain.QVotingItem.votingItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VotingRepositoryCustomImpl implements VotingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    //1차 코드
//    public List<Map<Long, List<String>>> getPollItemVotedNames(Long pollId) {
//        var queryVotingIds = queryFactory
//            .select(voting.id)
//            .from(voting)
//            .where(voting.pollId.eq(pollId))
//            .fetch();
//
//        var pollItemIdVoterMap = queryFactory
//            .select(votingItem.pollItemId, voting.voterName)
//            .from(voting)
//            .join(voting.items, votingItem)
//            .where(voting.id.in(queryVotingIds))
//            .fetch();
//
//        var groupedPollItemVotes = pollItemIdVoterMap.stream()
//            .collect(Collectors.groupingBy(
//                tuple -> tuple.get(votingItem.pollItemId),
//                Collectors.mapping(
//                    tuple -> tuple.get(voting.voterName).value(),
//                    Collectors.toList()
//                )
//            ));
//
//        return groupedPollItemVotes.entrySet().stream()
//            .map(entry -> Map.of(entry.getKey(), entry.getValue()))
//            .collect(Collectors.toList());
//    }


    //List<Map<String, Object>>
    // TODO 무조건 pollItemId 키에 대한 값이 Long 타입 버전
    // TODO 동적인 응답을 함
    public List<Map<String, Object>> getPollItemVotedNames(Long pollId) {
        var queryVotingIds = queryFactory
            .select(voting.id)
            .from(voting)
            .where(voting.pollId.eq(pollId))
            .fetch();

        var pollItemIdVoterMap = queryFactory
            .select(votingItem.pollItemId, voting.voterName)
            .from(voting)
            .join(voting.items, votingItem)
            .where(voting.id.in(queryVotingIds))
            .fetch();

        var groupedPollItemVotes = pollItemIdVoterMap.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(votingItem.pollItemId),
                Collectors.mapping(
                    tuple -> tuple.get(voting.voterName).value(),
                    Collectors.toList()
                )
            ));

        return groupedPollItemVotes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(tuple -> Map.of(
                "pollItemId", tuple.getKey(),
                "voterNames", tuple.getValue()
            ))
        .collect(Collectors.toList());
    }

    //빈 리스트 반환 TODO 수정 진행중 !!!
    //TODO 모든 PollItemId 를 반환
    //TODO voterName이 null일 경우 빈 리스트 반환
    //TODO 가능하다면 voterName 까지
//    public List<Map<String, Object>> getPollItemVotedNames(Long pollId, List<Long> pollItemIds) {
//
//        var queryVotingIds = queryFactory
//            .select(voting.id)
//            .from(voting)
//            .where(voting.pollId.eq(pollId))
//            .fetch();
//
//        var pollItemIdVoterMap = queryFactory
//            .select(votingItem.pollItemId, voting.voterName)
//            .from(voting)
//            .join(voting.items, votingItem)
//            .where(voting.id.in(queryVotingIds))
//            .fetch();
//
//        var groupedPollItemVotes = pollItemIdVoterMap.stream()
//            .collect(Collectors.groupingBy(
//                tuple -> tuple.get(votingItem.pollItemId),
//                Collectors.mapping(
//                    tuple -> tuple.get(voting.voterName).value(),
//                    Collectors.toList()
//                )
//            ));
//
//        return pollItemIds.stream()
//            .sorted()
//            .map(pollItemId -> {
//                var voterNames = groupedPollItemVotes.getOrDefault(pollItemId, List.of());
//
//                return Map.of(
//                    "pollItemId", pollItemId,
//                    "voterNames", voterNames
//                );
//            })
//            .collect(Collectors.toList());
//    }

}