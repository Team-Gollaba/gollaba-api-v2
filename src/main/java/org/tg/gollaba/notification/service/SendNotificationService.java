package org.tg.gollaba.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.compoenet.AppNotificationSender;

@Service
@RequiredArgsConstructor
public class SendNotificationService {
    private final AppNotificationSender appNotificationSender;

    @Transactional
    public void send(Requirement requirement) {
        var message = new AppNotificationSender.NotificationMessage(
            requirement.title(),
            requirement.content()
        );
        appNotificationSender.sendServerNotice(message);
    }

    public record Requirement(
        String title,
        String content
    ){
    }
}