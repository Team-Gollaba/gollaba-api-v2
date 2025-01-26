package org.tg.gollaba.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.notification.domain.AppNotificationHistory;

import java.util.List;

public interface AppNotificationHistoryRepositoryCustom {
    Page<AppNotificationHistory> findUserNotifications(Long userId, List<String> agentIds, Pageable pageable);
}
