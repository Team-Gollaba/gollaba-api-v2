package org.tg.gollaba.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;

@Repository
public class PollDailyStatsRepositoryCustomImpl extends AbstractStatsRepository implements PollDailyStatsRepositoryCustom {
    private static final String ORIGIN_TABLE_NAME = "poll_daily_stats";
    private static final String NEW_TABLE_NAME = "poll_daily_stats_new";
    private static final String BACKUP_TABLE_NAME = "poll_daily_stats_backup";
    
    public PollDailyStatsRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void createAllDailyStats() {
        createNewTable(NEW_TABLE_NAME, ORIGIN_TABLE_NAME);

        jdbcTemplate.execute("""
            INSERT INTO %s (poll_id, date, vote_count, read_count, favorites_count, created_at)
            SELECT p.id,
                   CURDATE() as date,
                   IF (vote_count_table.cnt IS NOT NULL, vote_count_table.cnt, 0),
                   p.read_count - ps.total_read_count,
                   IF (favorites_count_table.cnt IS NOT NULL, favorites_count_table.cnt, 0),
                   NOW()
            FROM poll p
            JOIN poll_stats ps ON ps.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, v.poll_id
                   FROM voting v
                   WHERE DATE(v.created_at) = CURDATE()
                   GROUP BY v.poll_id
                 ) vote_count_table
                 ON vote_count_table.poll_id = p.id
            LEFT JOIN (
                   SELECT COUNT(*) as cnt, f.poll_id
                   FROM favorites f
                   WHERE DATE(f.created_at) = CURDATE()
                   GROUP BY f.poll_id
                 ) favorites_count_table
                 ON favorites_count_table.poll_id = p.id;
            """.formatted(NEW_TABLE_NAME)
        );

        rotateTable(ORIGIN_TABLE_NAME, BACKUP_TABLE_NAME, NEW_TABLE_NAME);
    }
}
