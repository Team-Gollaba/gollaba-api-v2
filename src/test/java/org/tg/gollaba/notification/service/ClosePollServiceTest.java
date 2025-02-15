package org.tg.gollaba.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.compoenet.AppNotificationSender;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;

import static java.util.List.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


@ExtendWith(MockitoExtension.class)
public class ClosePollServiceTest {
    @InjectMocks
    private ClosePollService service;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private AppNotificationSender appNotificationSender;

    @Test
    void success(){
        //given
        var from = LocalDateTime.now().minusMinutes(5).plusSeconds(1);
        var to = LocalDateTime.now();
        var pollIds = of(1L, 2L);
        given(pollRepository.findPollIdsByEndAtBetween(from, to)).willReturn(pollIds);

        //when
        var throwable = catchThrowable(() -> service.close(from, to));

        //then
        assertThat(throwable).isNull();
        verify(pollRepository, times(1)).findPollIdsByEndAtBetween(from, to);
        verify(appNotificationSender, times(2)).pollClosed(anyLong());
    }
}