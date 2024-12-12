package org.tg.gollaba.voting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.tg.gollaba.voting.domain.QVotingItem.votingItem;

@RequiredArgsConstructor
public class VotingItemRepositoryCustomImpl implements VotingItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, Integer> votingCountByPollItemIds(List<Long> pollItemIds){
        return queryFactory
            .select(votingItem.pollItemId, votingItem.count())
            .from(votingItem)
            .where(votingItem.pollItemId.in(pollItemIds))
            .groupBy(votingItem.pollItemId)
            .fetch()
            .stream()
            .collect(toMap(
                tuple -> tuple.get(votingItem.pollItemId),
                tuple -> tuple.get(votingItem.count()).intValue()
            ));
    }
}
