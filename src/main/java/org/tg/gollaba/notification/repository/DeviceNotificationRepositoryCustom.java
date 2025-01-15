package org.tg.gollaba.notification.repository;

import org.tg.gollaba.notification.domain.DeviceNotification;

import java.util.List;

public interface DeviceNotificationRepositoryCustom {
    List<DeviceNotification> findNotiAllowUsers(List<Long> userIds);
}
