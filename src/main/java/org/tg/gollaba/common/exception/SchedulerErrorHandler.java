package org.tg.gollaba.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.time.LocalDateTime;


@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerErrorHandler implements ErrorHandler {
    private final ErrorNotificationSender errorNotificationSender;

    @Override
    public void handleError(Throwable e) {
        var message = """
            [스케줄러 오류] %s
            """.formatted(e.getMessage());

        errorNotificationSender.send(message, e);
        log.error("[스케줄러 오류][{}]", LocalDateTime.now(), e);
    }
}
