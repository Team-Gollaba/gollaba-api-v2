package org.tg.gollaba.stats.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;

public abstract class AbstractStatsRepository {
    protected final JdbcTemplate jdbcTemplate;

    protected AbstractStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected void createNewTable(String newTableName, String originTableName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS %s".formatted(newTableName));

        var originDDL = jdbcTemplate.queryForObject(
            "SHOW CREATE TABLE %s".formatted(originTableName),
            (rs, rowNum) -> rs.getString("Create Table")
        );

        if (StringUtils.isBlank(originDDL)) {
            throw new ServerException(Status.FAIL_TO_SQL, "Failed to get DDL for table poll_stats");
        }

        var newDDL = originDDL.replaceFirst(
            "CREATE TABLE `%s`".formatted(originTableName),
            "CREATE TABLE `%s`".formatted(newTableName)
        );

        jdbcTemplate.execute(newDDL);
    }

    protected void rotateTable(String originTableName,
                               String backupTableName,
                               String newTableName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS %s".formatted(backupTableName));
        jdbcTemplate.execute("RENAME TABLE %s TO %s, %s TO %s".formatted(originTableName, backupTableName, newTableName, originTableName));
    }
}
