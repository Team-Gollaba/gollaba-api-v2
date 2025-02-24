package org.tg.gollaba.notification.repository;

import static java.util.stream.Collectors.toMap;
import static org.tg.gollaba.common.support.QueryDslUtils.createColumnOrder;
import static org.tg.gollaba.notification.domain.QAppNotificationHistory.appNotificationHistory;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.notification.domain.AppNotificationHistory;

import java.util.LinkedHashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AppNotificationHistoryRepositoryCustomImpl implements AppNotificationHistoryRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AppNotificationHistory> findUserNotifications(Long userId, List<String> agentIds, Pageable pageable) {
        var orderBy = pageable.getSort()
            .stream()
            .map(order -> {
                if ("createdAt".equals(order.getProperty())) {
                    return createColumnOrder(appNotificationHistory.createdAt, order);
                }
                throw new IllegalArgumentException("존재하지 않는 컬럼입니다.");
            })
            .toArray(OrderSpecifier[]::new);

        var query = queryFactory
            .selectFrom(appNotificationHistory)
            .where(
                appNotificationHistory.userId.eq(userId),
                appNotificationHistory.agentId.in(agentIds),
                appNotificationHistory.status.eq(AppNotificationHistory.Status.SUCCESS)
            )
            .orderBy(orderBy)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        var distinctNotifications = query.stream()
            .collect(toMap(
                AppNotificationHistory::deepLink,
                notification -> notification,
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ))
            .values()
            .stream()
            .toList();

        var totalCount = distinctNotifications.size();

        if (totalCount == 0) {
            return Page.empty();
        }

        var pagedNotifications = distinctNotifications.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();

        return new PageImpl<>(pagedNotifications, pageable, totalCount);
    }
}