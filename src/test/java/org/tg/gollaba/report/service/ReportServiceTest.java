package org.tg.gollaba.report.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.report.domain.PollReport;
import org.tg.gollaba.report.repository.PollReportRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @InjectMocks
    private ReportService service;
    @Mock
    private PollReportRepository pollReportRepository;

    @Test
    void success_registeredUser() {
        // given
        var requirement = new ReportService.Requirement(
            1L,
            "userId",
            PollReport.ReporterType.REGISTERED_USER,
            PollReport.ReportType.SPAM,
            "content"
        );

        // when
        var throwable = catchThrowable(()  -> service.report(requirement));

        // then
        assertThat(throwable).isNull();
        verify(pollReportRepository, times(1)).save(any(PollReport.class));
        var argumentCaptor = ArgumentCaptor.forClass(PollReport.class);
        verify(pollReportRepository).save(argumentCaptor.capture());
        var savedReport = argumentCaptor.getValue();
        assertThat(savedReport.pollId()).isEqualTo(requirement.pollId());
        assertThat(savedReport.reporterKey()).isEqualTo(requirement.reporterKey());
        assertThat(savedReport.reporterType()).isEqualTo(requirement.reporterType());
        assertThat(savedReport.reportType()).isEqualTo(requirement.reportType());
        assertThat(savedReport.content()).isEqualTo(requirement.content());
    }

    @Test
    void success_unregisteredUser() {
        // given
        var requirement = new ReportService.Requirement(
            1L,
            "255.255.255.1",
            PollReport.ReporterType.UNREGISTERED_USER,
            PollReport.ReportType.SPAM,
            "content"
        );

        // when
        var throwable = catchThrowable(() -> service.report(requirement));

        // then
        assertThat(throwable).isNull();
        verify(pollReportRepository, times(1)).save(any(PollReport.class));
        var argumentCaptor = ArgumentCaptor.forClass(PollReport.class);
        verify(pollReportRepository).save(argumentCaptor.capture());
        var savedReport = argumentCaptor.getValue();
        assertThat(savedReport.pollId()).isEqualTo(requirement.pollId());
        assertThat(savedReport.reporterKey()).isEqualTo(requirement.reporterKey());
        assertThat(savedReport.reporterType()).isEqualTo(requirement.reporterType());
        assertThat(savedReport.reportType()).isEqualTo(requirement.reportType());
        assertThat(savedReport.content()).isEqualTo(requirement.content());

    }
}