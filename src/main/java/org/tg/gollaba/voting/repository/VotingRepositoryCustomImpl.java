package org.tg.gollaba.voting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.tg.gollaba.voting.domain.QVoting.voting;
import static org.tg.gollaba.voting.domain.QVotingItem.votingItem;

@RequiredArgsConstructor
public class VotingRepositoryCustomImpl implements VotingRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, Map<Long, Integer>> votingCountMapByPollId(List<Long> pollIds) {
        return queryFactory
            .select(voting.pollId, votingItem.pollItemId, votingItem.count())
            .from(voting)
            .join(voting.items, votingItem)
            .where(voting.pollId.in(pollIds))
            .groupBy(voting.pollId, votingItem.pollItemId)
            .fetch()
            .stream()
            .collect(
                Collectors.groupingBy(
                    t -> t.get(voting.pollId),
                    Collectors.toMap(
                        t -> t.get(votingItem.pollItemId),
                        t -> Optional.ofNullable(t.get(votingItem.count()))
                            .map(Long::intValue)
                            .orElse(0)
                    )
                )
            );
    }

    @Override
    public List<Long> findPollIdsByUserId(Long userId) {
        return queryFactory
            .select(voting.pollId)
            .from(voting)
            .where(voting.userId.eq(userId))
            .fetch();
    }
}
