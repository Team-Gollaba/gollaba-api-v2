package org.tg.gollaba.notification.repository;

import org.tg.gollaba.poll.domain.Poll;

import java.util.List;
import java.util.Set;

public interface AppNotificationHistoryRepositoryCustom {
    Set<Long> findExcludeUsers(List<Poll> polls);
}
