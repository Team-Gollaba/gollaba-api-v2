package org.tg.gollaba.common.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;


@Component
@Slf4j
@RequiredArgsConstructor
public class FcmClient {
    public void sendMessage(Request request) {
        var message = createMessage(request);

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("fail to send fcm message", e);
            throw new ServerException(Status.FAIL_TO_SEND_FCM_MESSAGE);
        }
    }

    public Message createMessage(Request request) {
        var notification = Notification
            .builder()
            .setTitle(request.title())
            .setBody(request.content())
            .build();

        return Message
            .builder()
            .setNotification(notification)
            .setToken(request.agentId())
            .build();
    }

    public record Request(
        String agentId,
        String title,
        String content
    ) {
    }
}
