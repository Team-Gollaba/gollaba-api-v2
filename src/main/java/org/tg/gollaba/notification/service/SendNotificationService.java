package org.tg.gollaba.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.compoenet.TestAppNotificationSender;

@Service
@RequiredArgsConstructor
public class SendNotificationService {
    private final TestAppNotificationSender testAppNotificationSender;
    @Transactional
    public void send(Requirement requirement) {
        var message = new TestAppNotificationSender.NotificationMessage(
            requirement.title(),
            requirement.content()
        );
        testAppNotificationSender.sendServerNotice(message);
    }

    public record Requirement(
        String title,
        String content
    ){
    }
}