package org.tg.gollaba.report.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class ReportFixture implements TestFixture<PollReport> {
    private Long id = 1L;
    private Long pollId = 1L;
    private PollReport.ReporterType reporterType = PollReport.ReporterType.UNREGISTERED_USER;
    private PollReport.ReportType reportType = PollReport.ReportType.SPAM;
    private String reporterKey = "ipAddress";
    private String content = "testContent";

    @Override
    public PollReport build(){
        var report = new PollReport();
            FixtureReflectionUtils.reflect(report, this);
        return report;
    }
}
