package org.tg.gollaba.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.report.domain.PollReport;

@Repository
public interface PollReportRepository extends JpaRepository<PollReport, Long> {
}
