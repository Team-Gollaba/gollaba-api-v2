package org.tg.gollaba.notification.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;

@Table(name = "app_notification_history")
@Entity
@Getter
@ToString
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppNotificationHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String agentId;

    @Column(nullable = false)
    private long deviceNotificationId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String failReason;

    @Column
    private String eventId;

    @Column
    private String deepLink;

    public AppNotificationHistory(Type type,
                                  Status status,
                                  Long userId,
                                  String agentId,
                                  long deviceNotificationId,
                                  String title,
                                  String content,
                                  String failReason,
                                  String eventId,
                                  String deepLink) {
        this.type = type;
        this.status = status;
        this.userId = userId;
        this.agentId = agentId;
        this.deviceNotificationId = deviceNotificationId;
        this.title = title;
        this.content = content;
        this.failReason = failReason;
        this.eventId = eventId;
        this.deepLink = deepLink;
    }

    public enum Type {
        POLL_TERMINATE,
        FAVORITE,
        VOTING,
        SERVER_NOTICE
    }

    public enum Status {
        SUCCESS,
        FAILURE
    }
}