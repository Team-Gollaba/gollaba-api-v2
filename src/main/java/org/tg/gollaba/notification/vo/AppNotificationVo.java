package org.tg.gollaba.notification.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.tg.gollaba.poll.controller.serializer.HashIdSerializer;

public record AppNotificationVo(
    Long notificationId,
    Long userId,
    String agentId,
    @JsonSerialize(using = HashIdSerializer.class)
    String eventId,
    String deepLink,
    String title,
    String content
) {}
