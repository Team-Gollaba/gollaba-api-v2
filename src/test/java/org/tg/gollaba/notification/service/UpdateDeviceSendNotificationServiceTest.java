package org.tg.gollaba.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.domain.NotificationDeviceFixture;
import org.tg.gollaba.notification.repository.NotificationDeviceRepository;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateDeviceSendNotificationServiceTest {
    @InjectMocks
    private UpdateNotificationDeviceService service;

    @Mock
    private NotificationDeviceRepository notificationDeviceRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void success(){
        //given
        var user = new UserFixture().build();
        var deviceNotification = new NotificationDeviceFixture()
            .setAllowsNotification(true)
            .build();
        var requirement = new UpdateNotificationDeviceService.Requirement(
            1L,
            "12",
            false
        );
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(notificationDeviceRepository.findByUserIdAndAgentIdAndDeletedAtIsNull(1L, "12"))
            .willReturn(Optional.of(deviceNotification));

        // when
        var throwable = catchThrowable(() -> service.update(requirement));

        // then
        assertThat(throwable).isNull();
        verify(notificationDeviceRepository, times(1)).save(any(NotificationDevice.class));
        var argumentCaptor = ArgumentCaptor.forClass(NotificationDevice.class);
        verify(notificationDeviceRepository).save(argumentCaptor.capture());
        var capturedAppNotification = argumentCaptor.getValue();
        assertThat(capturedAppNotification.allowsNotification()).isEqualTo(requirement.allowsNotification());
    }
}
