package org.tg.gollaba.stats.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.tg.gollaba.stats.domain.QPollSearchStats.pollSearchStats;

@Repository
@RequiredArgsConstructor
public class PollSearchStatsRepositoryCustomImpl implements PollSearchStatsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Map<String, Object>> findTop10SearchedWords() {
        var result = queryFactory
            .select(pollSearchStats.searchedWord, pollSearchStats.searchedWord.count())
            .from(pollSearchStats)
            .groupBy(pollSearchStats.searchedWord)
            .orderBy(pollSearchStats.searchedWord.count().desc())
            .limit(10)
            .fetch();

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result.stream()
            .map(tuple -> {
                Map<String, Object> map = new HashMap<>();
                map.put("searchedWord", tuple.get(pollSearchStats.searchedWord));
                map.put("searchCount", tuple.get(pollSearchStats.searchedWord.count()));
                return map;
            })
            .collect(Collectors.toList());
    }
}