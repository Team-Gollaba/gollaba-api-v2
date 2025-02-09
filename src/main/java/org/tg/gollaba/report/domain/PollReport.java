package org.tg.gollaba.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Getter
@Accessors(fluent = true)
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollReport extends BaseEntityForOnlyCreatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long pollId;

    @Column
    private String reporterKey;

    @Column
    @Enumerated(EnumType.STRING)
    private ReporterType reporterType;

    @Column
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column
    private String content;

    public PollReport(Long pollId,
                      String reporterKey,
                      ReporterType reporterType,
                      ReportType reportType,
                      String content) {
        this.pollId = pollId;
        this.reporterKey = reporterKey;
        this.reporterType = reporterType;
        this.reportType = reportType;
        this.content = content;
    }

    public enum ReporterType {
        REGISTERED_USER,
        UNREGISTERED_USER
    }

    public enum ReportType {
        SPAM,
        HARASSMENT,
        ADVERTISEMENT,
        MISINFORMATION,
        COPYRIGHT_INFRINGEMENT,
        ETC
    }
}
