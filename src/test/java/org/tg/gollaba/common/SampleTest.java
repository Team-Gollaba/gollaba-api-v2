package org.tg.gollaba.common;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.tg.gollaba.voting.domain.QVoting.voting;
import static org.tg.gollaba.voting.domain.QVotingItem.votingItem;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
public class SampleTest {
    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    void test() {
        //SELECT v.poll_id, vi.poll_item_id, COUNT(*)
        //FROM voting_item vi
        //JOIN voting v ON v.id = vi.voting_id
        //WHERE v.poll_id in (573, 574, 575)
        //GROUP BY vi.poll_item_id;

        Map<Long, Map<Long, Integer>> tuples = queryFactory
            .select(voting.pollId, votingItem.pollItemId, votingItem.count())
            .from(voting)
            .join(voting.items, votingItem)
            .where(voting.pollId.in(573L, 574L, 575L))
            .groupBy(votingItem.pollItemId)
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

        System.out.println(tuples);
    }
}
