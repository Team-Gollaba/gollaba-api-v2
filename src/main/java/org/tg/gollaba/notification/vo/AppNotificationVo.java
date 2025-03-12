package org.tg.gollaba.notification.vo;

public record AppNotificationVo(
    Long notificationId,
    Long userId,
    String deepLink,
    String title,
    String content
) {}
