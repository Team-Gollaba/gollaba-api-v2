package org.tg.gollaba.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.compoenet.TestAppNotificationSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SendNotificationServiceTest {
    @InjectMocks
    private SendNotificationService service;
    @Mock
    private TestAppNotificationSender testAppNotificationSender;

    @Test
    void success(){
        //given
        var message = message();

        //when
        var throwable = catchThrowable(() -> service.send(message));

        //then
        assertThat(throwable).isNull();
        verify(testAppNotificationSender, times(1)).sendServerNotice(any(TestAppNotificationSender.NotificationMessage.class));
    }

    private SendNotificationService.Requirement message(){
        return new SendNotificationService.Requirement(
            "title",
            "content"
        );
    }
}
