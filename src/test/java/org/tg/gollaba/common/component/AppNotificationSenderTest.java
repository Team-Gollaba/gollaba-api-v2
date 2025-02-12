package org.tg.gollaba.common.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.client.FcmClient;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.notification.domain.AppNotificationHistory;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.domain.DeviceNotificationFixture;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.List.*;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AppNotificationSenderTest {

    @InjectMocks
    private AppNotificationSender sender;

    @Mock
    private FcmClient fcmClient;

    @Mock
    private DeviceNotificationRepository deviceNotificationRepository;

    @Mock
    private AppNotificationHistoryRepository appNotificationHistoryRepository;

    @Mock
    private HashIdHandler hashIdHandler;

    @Test
    void success(){
        //given
        var targetDevices = targetDevices();
        var pollIds = of(1L, 2L);
        var userIds = of(1L, 2L);
        given(deviceNotificationRepository.findNotiAllowUsers(userIds))
            .willReturn(targetDevices);
        doNothing().when(fcmClient)
            .sendMessage(any(FcmClient.Request.class));
        given(hashIdHandler.encode(1L)).willReturn("pollHashId");

        //when
        var throwable = catchThrowable(() -> sender.sendPollNotifications(pollIds));

        //then
        assertThat(throwable).isNull();
        verify(fcmClient, times(2)).sendMessage(any(FcmClient.Request.class));
        verify(appNotificationHistoryRepository, times(2)).save(any(AppNotificationHistory.class));
        var argumentCaptor = ArgumentCaptor.forClass(AppNotificationHistory.class);
        verify(appNotificationHistoryRepository, times(2)).save(argumentCaptor.capture());
        var capturedHistories = argumentCaptor.getAllValues();
        assertThat(capturedHistories).hasSize(2);
        var history = capturedHistories.get(0);
        assertThat(history.title()).isEqualTo("투표가 종료되었습니다.");
        assertThat(history.content()).isEqualTo("종료된 투표의 결과를 확인하세요.");
        assertThat(history.status()).isEqualTo(AppNotificationHistory.Status.SUCCESS);
        assertThat(history.deepLink()).isEqualTo("Gollaba-app//notification?pollHashId=pollHashId");
    }

    private List<DeviceNotification> targetDevices() {
        return Stream.of(
            new DeviceNotificationFixture()
                .setUserId(1L)
                .setAgentId("1")
                .setAllowsNotification(true)
                .build(),
            new DeviceNotificationFixture()
                .setUserId(2L)
                .setAgentId("2")
                .setAllowsNotification(true)
                .build()
        ).collect(Collectors.toList());
    }
}
