package org.tg.gollaba.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tg.gollaba.notification.domain.NotificationDevice;

import java.util.List;
import java.util.Optional;

public interface NotificationDeviceRepository extends JpaRepository<NotificationDevice, Long> {

    Optional<NotificationDevice> findByUserIdAndAgentIdAndDeletedAtIsNull(Long userId, String agentId);

    @Query("""
        SELECT dn
        FROM NotificationDevice dn
        WHERE dn.userId = :userId
        AND dn.deletedAt is null
    """)
    List<NotificationDevice> findAllActiveByUserId(long userId);

    @Query("""
        SELECT dn
        FROM NotificationDevice dn
        WHERE dn.userId in :userIds
        AND dn.deletedAt is null
    """)
    List<NotificationDevice> findAllActiveByUserIds(List<Long> userIds);
}