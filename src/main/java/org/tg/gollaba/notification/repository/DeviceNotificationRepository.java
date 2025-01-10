package org.tg.gollaba.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.notification.domain.DeviceNotification;

import java.util.List;
import java.util.Optional;

public interface DeviceNotificationRepository extends JpaRepository<DeviceNotification, Long> {

    boolean existsByUserIdAndDeviceName(Long userId, String deviceName);

    Optional<DeviceNotification> findByUserIdAndAgentId(Long userId, String agentId);

    List<DeviceNotification> findAllByAllowsNotificationTrue();
}