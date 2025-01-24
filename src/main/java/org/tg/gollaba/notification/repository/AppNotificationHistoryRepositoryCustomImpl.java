package org.tg.gollaba.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AppNotificationHistoryRepositoryCustomImpl implements AppNotificationHistoryRepositoryCustom{
    private final JPAQueryFactory queryFactory;

}