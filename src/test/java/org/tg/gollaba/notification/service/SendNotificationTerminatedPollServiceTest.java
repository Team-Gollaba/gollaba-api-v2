package org.tg.gollaba.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.domain.DeviceNotificationFixture;
import org.tg.gollaba.notification.repository.AppNotificationHistoryRepository;
import org.tg.gollaba.notification.repository.DeviceNotificationRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class SendNotificationTerminatedPollServiceTest {
    @InjectMocks
    private SendNotificationTerminatedPollService service;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private DeviceNotificationRepository deviceNotificationRepository;
    @Mock
    private AppNotificationHistoryRepository appNotificationHistoryRepository;
    @Mock
    private AppNotificationSender appNotificationSender;

    @Test
    void success(){
        //given
        var aggregationDate = LocalDate.now();
        var polls = polls();
        given(pollRepository.findTerminatedPollsBefore(aggregationDate)).willReturn(polls);
        given(appNotificationHistoryRepository.findExcludeUsers(polls)).willReturn(excludeUserIds());
        given(deviceNotificationRepository.findNotiAllowUsers(anyList())).willReturn(targetDevices());

        //when
        var throwable = catchThrowable(() -> service.send(aggregationDate));

        //then
        assertThat(throwable).isNull();
        verify(appNotificationSender, times(1)).send(any(AppNotificationSender.NotificationMessage.class));
    }

    private List<Poll> polls() {
        return List.of(
            new PollFixture().setId(1L).setUserId(1L).build(),
            new PollFixture().setId(2L).setUserId(2L).build()
        );
    }
    private Set<Long> excludeUserIds() {
        return Set.of(3L, 4L, 5L);
    }
    private List<DeviceNotification> targetDevices(){
        return List.of(
            new DeviceNotificationFixture().setId(1L).build(),
            new DeviceNotificationFixture().setId(2L).build()
        );
    }
}