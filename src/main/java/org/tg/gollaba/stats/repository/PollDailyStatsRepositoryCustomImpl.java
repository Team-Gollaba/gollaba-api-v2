package org.tg.gollaba.stats.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.tg.gollaba.stats.domain.QPollDailyStats.pollDailyStats;

@Repository
@RequiredArgsConstructor
public class PollDailyStatsRepositoryCustomImpl implements PollDailyStatsRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    @Override
    public void createAllDailyStats(LocalDate aggregationDate) {
        var startsAt = aggregationDate.minusDays(1).atStartOfDay();
        var endsAt = aggregationDate.plusDays(1).atStartOfDay().minusSeconds(1);

        jdbcTemplate.update("""
            INSERT INTO poll_daily_stats (poll_id, aggregation_date, vote_count, read_count, favorites_count, created_at)
            SELECT p.id,
                   ?,
                   COALESCE(vote_count_table.cnt, 0),
                      p.read_count - ps.total_read_count,
                   COALESCE(favorites_count_table.cnt, 0),
                   NOW()
            FROM poll p
            JOIN poll_stats ps ON ps.poll_id = p.id AND ps.aggregation_date = ?
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, v.poll_id
                   FROM voting v
                   WHERE v.created_at BETWEEN ? AND ?
                   GROUP BY v.poll_id
                 ) vote_count_table
                 ON vote_count_table.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, f.poll_id
                   FROM favorites f
                   WHERE f.created_at BETWEEN ? AND ?
                   GROUP BY f.poll_id
                 ) favorites_count_table
                 ON favorites_count_table.poll_id = p.id;
            """,
            aggregationDate,
            aggregationDate,
            startsAt,
            endsAt,
            startsAt,
            endsAt
        );
    }

    @Override
    public List<Long> findTrendingPollIds(LocalDate aggregationDate, int limit) {
        return queryFactory
            .select(pollDailyStats.pollId)
            .from(pollDailyStats)
            .where(pollDailyStats.aggregationDate.eq(aggregationDate))
            .orderBy(
                pollDailyStats.voteCount.desc(),
                pollDailyStats.readCount.desc(),
                pollDailyStats.favoritesCount.desc(),
                pollDailyStats.pollId.desc())
            .limit(limit)
            .fetch();
    }
}