package org.tg.gollaba.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.tg.gollaba.notification.domain.DeviceNotification;

import java.util.ArrayList;
import java.util.List;

import static org.tg.gollaba.notification.domain.QDeviceNotification.deviceNotification;

@Repository
@RequiredArgsConstructor
public class DeviceNotificationRepositoryCustomImpl implements DeviceNotificationRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<DeviceNotification> findNotiAllowUsers(List<Long> userIds) {
        return queryFactory.select(deviceNotification)
            .from(deviceNotification)
            .where(
                deviceNotification.userId.in(userIds),
                deviceNotification.allowsNotification.isTrue()
            )
            .fetch();
    }
}
