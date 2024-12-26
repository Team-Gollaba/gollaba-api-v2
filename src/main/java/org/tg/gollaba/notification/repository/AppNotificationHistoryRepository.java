package org.tg.gollaba.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.notification.domain.AppNotificationHistory;

public interface AppNotificationHistoryRepository extends JpaRepository<AppNotificationHistory, Long> {
}
