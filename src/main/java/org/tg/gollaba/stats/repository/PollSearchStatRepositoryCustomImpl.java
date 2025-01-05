package org.tg.gollaba.stats.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.tg.gollaba.stats.domain.QPollSearchStat.pollSearchStat;

@Repository
@RequiredArgsConstructor
public class PollSearchStatRepositoryCustomImpl implements PollSearchStatRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Map<String, Object>> findTop10SearchedWords() {
        var result = queryFactory
            .select(pollSearchStat.searchedWord, pollSearchStat.searchedWord.count())
            .from(pollSearchStat)
            .groupBy(pollSearchStat.searchedWord)
            .orderBy(pollSearchStat.searchedWord.count().desc())
            .limit(10)
            .fetch();

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result.stream()
            .map(tuple -> {
                Map<String, Object> map = new HashMap<>();
                map.put("searchedWord", tuple.get(pollSearchStat.searchedWord));
                map.put("searchCount", tuple.get(pollSearchStat.searchedWord.count()));
                return map;
            })
            .collect(Collectors.toList());
    }
}