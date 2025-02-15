package org.tg.gollaba.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.repository.NotificationDeviceRepository;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateNotificationDeviceServiceTest {
    @InjectMocks
    private CreateNotificationDeviceService service;

    @Mock
    private NotificationDeviceRepository notificationDeviceRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void success(){
        //given
        var user = new UserFixture().build();
        var requirement = new CreateNotificationDeviceService.Requirement(
            1L,
            "12",
            NotificationDevice.OperatingSystemType.ANDROID,
            "deviceName",
            true
        );
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(notificationDeviceRepository.findByUserIdAndAgentIdAndDeletedAtIsNull(1L, "12")).willReturn(Optional.empty());

        // when
        var throwable = catchThrowable(() -> service.create(requirement));

        // then
        assertThat(throwable).isNull();
        verify(notificationDeviceRepository, times(1)).save(any(NotificationDevice.class));

        var argumentCaptor = ArgumentCaptor.forClass(NotificationDevice.class);
        verify(notificationDeviceRepository).save(argumentCaptor.capture());

        var capturedAppNotification = argumentCaptor.getValue();
        assertThat(capturedAppNotification.userId()).isEqualTo(requirement.userId());
        assertThat(capturedAppNotification.agentId()).isEqualTo(requirement.agentId());
        assertThat(capturedAppNotification.osType()).isEqualTo(NotificationDevice.OperatingSystemType.ANDROID);
        assertThat(capturedAppNotification.deviceName()).isEqualTo(requirement.deviceName());
        assertThat(capturedAppNotification.allowsNotification()).isEqualTo(requirement.allowsNotification());
    }
}
