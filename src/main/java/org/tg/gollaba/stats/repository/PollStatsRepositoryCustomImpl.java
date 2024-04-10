package org.tg.gollaba.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;

@Repository
public class PollStatsRepositoryCustomImpl extends AbstractStatsRepository implements PollStatsRepositoryCustom {
    private static final String ORIGIN_TABLE_NAME = "poll_stats";
    private static final String NEW_TABLE_NAME = "poll_stats_new";
    private static final String BACKUP_TABLE_NAME = "poll_stats_backup";

    protected PollStatsRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void createAllStats() {
        createNewTable(NEW_TABLE_NAME, ORIGIN_TABLE_NAME);

        jdbcTemplate.execute("""
            INSERT INTO %s (poll_id, total_vote_count, total_read_count, total_favorites_count, created_at)
            SELECT p.id,
                   IF (vote_count_table.cnt IS NOT NULL, vote_count_table.cnt, 0),
                   p.read_count,
                   IF (favorites_count_table.cnt IS NOT NULL, favorites_count_table.cnt, 0),
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
            """.formatted(NEW_TABLE_NAME)
        );

        rotateTable(ORIGIN_TABLE_NAME, BACKUP_TABLE_NAME, NEW_TABLE_NAME);
    }
}
