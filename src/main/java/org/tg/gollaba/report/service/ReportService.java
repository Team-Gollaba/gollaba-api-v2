package org.tg.gollaba.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.report.domain.PollReport;
import org.tg.gollaba.report.repository.PollReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PollReportRepository pollReportRepository;

    @Transactional
    public void report(Requirement requirement){
        var report = new PollReport(
            requirement.pollId(),
            requirement.reporterKey(),
            requirement.reporterType(),
            requirement.reportType(),
            requirement.content()
            );
        pollReportRepository.save(report);
    }

    public record Requirement(
        Long pollId,
        String reporterKey,
        PollReport.ReporterType reporterType,
        PollReport.ReportType  reportType,
        String content
    ){}
}