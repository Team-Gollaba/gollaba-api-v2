package org.tg.gollaba.notification.repository;

import static org.tg.gollaba.notification.domain.QAppNotificationHistory.appNotificationHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.notification.domain.AppNotificationHistory;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AppNotificationHistoryRepositoryCustomImpl implements AppNotificationHistoryRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AppNotificationHistory> findUserNotifications(Long userId, List<String> agentIds, Pageable pageable) {
        var totalCount = queryFactory
            .select(appNotificationHistory.count())
            .from(appNotificationHistory)
            .where(
                appNotificationHistory.userId.eq(userId),
                appNotificationHistory.agentId.in(agentIds),
                appNotificationHistory.status.eq(AppNotificationHistory.Status.SUCCESS)
            )
            .fetchOne();

        if (totalCount == null || totalCount == 0) {
            return Page.empty();
        }

        var query = queryFactory
            .selectFrom(appNotificationHistory)
            .where(
                appNotificationHistory.userId.eq(userId),
                appNotificationHistory.agentId.in(agentIds),
                appNotificationHistory.status.eq(AppNotificationHistory.Status.SUCCESS)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(query, pageable, totalCount);
    }
}