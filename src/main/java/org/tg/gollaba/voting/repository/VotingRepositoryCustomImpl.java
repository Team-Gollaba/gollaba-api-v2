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

    public List<Map<String, Object>> getPollItemVotedNames(Long pollId, List<Long> pollItemIds) {
        var queryVotingIds = queryFactory
            .select(voting.id)
            .from(voting)
            .where(voting.pollId.eq(pollId)
              .and(voting.deletedAt.isNull())
            )
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

        return pollItemIds.stream()
            .sorted()
            .map(pollItemId -> {
                var voterNames = groupedPollItemVotes.getOrDefault(pollItemId, List.of());

                return Map.of(
                    "pollItemId", pollItemId,
                    "voterNames", voterNames
                );
            })
            .collect(Collectors.toList());
    }
}