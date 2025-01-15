package org.tg.gollaba.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.poll.domain.Poll;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.tg.gollaba.notification.domain.QAppNotificationHistory.appNotificationHistory;

@Repository
@RequiredArgsConstructor
public class AppNotificationHistoryRepositoryCustomImpl implements AppNotificationHistoryRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Set<Long> findExcludeUsers(List<Poll> polls) {
        var userIds = polls.stream()
            .map(Poll::userId)
            .filter(Objects::nonNull)
            .toList();

        var pollIds = polls.stream()
            .map(Poll::id)
            .toList();

        return new HashSet<>(queryFactory
            .select(appNotificationHistory.userId)
            .from(appNotificationHistory)
            .where(
                appNotificationHistory.userId.in(userIds),
                appNotificationHistory.pollId.in(pollIds),
                appNotificationHistory.type.eq(AppNotificationHistory.Type.POLL_TERMINATE),
                appNotificationHistory.status.eq(AppNotificationHistory.Status.SUCCESS)
            )
            .fetch()
        );
    }
}