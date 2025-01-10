package org.tg.gollaba.notification.domain;

import org.tg.gollaba.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Table(name = "device_notification")
@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String agentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperatingSystemType osType;

    @Column(nullable = false)
    private String deviceName;

    @Column
    private boolean allowsNotification;

    public DeviceNotification(Long userId,
                           String agentId,
                           OperatingSystemType osType,
                           String deviceName,
                           boolean allowsNotification) {
        this.userId = userId;
        this.agentId = agentId;
        this.osType = osType;
        this.deviceName = deviceName;
        this.allowsNotification = allowsNotification;
        validate();
    }

    public void update(boolean allowsNotification) {
        if (allowsNotification != this.allowsNotification) {
            this.allowsNotification = allowsNotification;
        }

        validate();
    }

    void validate() {
        if (userId == null) {
            throw new InvalidAppNotificationException(
                String.format("userId는 null일 수 없습니다. currentValue: %d", userId)
            );
        }

        if (osType == null) {
            throw new InvalidAppNotificationException(
                "osType은 필수입니다."
            );
        }

        if (deviceName == null || deviceName.isBlank()) {
            throw new InvalidAppNotificationException(
                "deviceName은 비거나 공백일 수 없습니다. currentValue: %s".formatted(deviceName)
            );
        }
    }

    public enum OperatingSystemType {
        ANDROID,
        IOS
    }

    public static class InvalidAppNotificationException extends IllegalArgumentException {
        public InvalidAppNotificationException(String message) {
            super(message);
        }
    }
}
