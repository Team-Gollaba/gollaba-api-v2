package org.tg.gollaba.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;

@Repository
@RequiredArgsConstructor
public class PollDailyStatsRepositoryCustomImpl implements PollDailyStatsRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createAllDailyStats() {
        jdbcTemplate.execute("""
            INSERT INTO poll_daily_stats (poll_id, aggregation_date, vote_count, read_count, favorites_count, created_at)
            SELECT p.id,
            NOW(),
            COALESCE(vote_count_table.cnt, 0),
            p.read_count - ps.total_read_count,
            COALESCE(favorites_count_table.cnt, 0),
            NOW()
            FROM poll p
            JOIN poll_stats ps ON ps.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, v.poll_id
                   FROM voting v
                   WHERE DATE(v.created_at) = CURRENT_DATE
                   GROUP BY v.poll_id
                 ) vote_count_table
                 ON vote_count_table.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, f.poll_id
                   FROM favorites f
                   WHERE DATE(f.created_at) = CURRENT_DATE
                   GROUP BY f.poll_id
                 ) favorites_count_table
                 ON favorites_count_table.poll_id = p.id;
            """
        );
    }
}