package org.tg.gollaba.common.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.ServerException;

import static com.google.firebase.ErrorCode.NOT_FOUND;
import static org.tg.gollaba.common.support.Status.FAIL_TO_SEND_FCM_MESSAGE;


@Component
@Slf4j
@RequiredArgsConstructor
public class FcmClient {

    public void sendMessage(Request request) throws AgentNotFoundException {
        var message = createMessage(request);

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getErrorCode() == NOT_FOUND) {
                throw new AgentNotFoundException();
            }

            log.error("fail to send fcm message", e);
            var errorMessage = FAIL_TO_SEND_FCM_MESSAGE.message() + " 사유: " + e.getMessage();
            throw new ServerException(FAIL_TO_SEND_FCM_MESSAGE, errorMessage);
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
            .putData("deepLink", request.deepLink())
            .build();
    }

    public static class AgentNotFoundException extends RuntimeException {
        public AgentNotFoundException() {
            super("fail to send fcm message: agent not found");
        }
    }

    public record Request(
        String agentId,
        String title,
        String content,
        String deepLink
    ) {
    }
}
