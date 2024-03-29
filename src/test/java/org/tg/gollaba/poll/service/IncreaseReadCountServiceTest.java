package org.tg.gollaba.poll.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class IncreaseReadCountServiceTest {

    @InjectMocks
    private IncreaseReadCountService service;

    @Mock
    private PollRepository pollRepository;

    @Test
    void success() {
        //given
        var poll = new PollFixture()
            .setReadCount(0)
            .build();
        given(pollRepository.findById(poll.id())).willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(() -> service.increase(poll.id()));

        //then
        assertThat(throwable).isNull();
        verify(pollRepository, times(1)).save(any(Poll.class));

        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(pollRepository).save(argumentCaptor.capture());

        var capturedPoll = argumentCaptor.getValue();
        assertThat(capturedPoll.readCount()).isEqualTo(1);
    }
}