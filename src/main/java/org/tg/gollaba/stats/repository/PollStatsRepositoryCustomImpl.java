package org.tg.gollaba.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class PollStatsRepositoryCustomImpl implements PollStatsRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createAllStats(LocalDate aggregationDate) {
        jdbcTemplate.update("""
            INSERT INTO poll_stats (poll_id, aggregation_date, total_vote_count, total_read_count, total_favorites_count, created_at)
            SELECT p.id,
                   ?,
                   COALESCE(vote_count_table.cnt, 0),
                   p.read_count,
                   COALESCE(favorites_count_table.cnt, 0),
                   NOW()
            FROM poll p
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, v.poll_id
                   FROM voting v
                   GROUP BY v.poll_id
                 ) vote_count_table
                 ON vote_count_table.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, f.poll_id
                   FROM favorites f
                   GROUP BY f.poll_id
                 ) favorites_count_table
                 ON favorites_count_table.poll_id = p.id;
            """,
            aggregationDate
        );
    }
}