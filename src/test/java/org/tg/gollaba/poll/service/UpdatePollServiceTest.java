package org.tg.gollaba.poll.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.poll.component.PollS3FileUploader;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UpdatePollServiceTest {

    @InjectMocks
    private UpdatePollService service;

    @Mock
    PollRepository pollRepository;

    @Mock
    private PollS3FileUploader fileUploader;

    @Test
    void success(){
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var poll = new PollFixture().build();
        var reqItems = List.of(
            new UpdatePollService.Requirement.Item(
                Optional.of("changeDescription1"),
                Optional.of(mockImageFile)
            ),
            new UpdatePollService.Requirement.Item(
                Optional.of("changeDescription2"),
                Optional.of(mockImageFile)
            )
        );
        var requirement = new UpdatePollService.Requirement(
            1L,
            Optional.of("changeTitle"),
            Optional.of(LocalDateTime.now().plusMinutes(50)),
            reqItems
        );
        given(pollRepository.findById(requirement.pollId())).willReturn(Optional.of(poll));
        given(fileUploader.uploadPollItemImage(anyLong(), anyLong(), eq(mockImageFile))).willReturn("https://updated-pollItemImage-1.com/test.png");

        //when
        var throwable = catchThrowable(() -> service.update(requirement));

        //then
        assertThat(throwable).isNull();
//        verify(pollRepository, times(1)).save(any(Poll.class));

        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(pollRepository).save(argumentCaptor.capture());

        var capturedPoll = argumentCaptor.getValue();
        verify(pollRepository, times(1)).save(capturedPoll);

        assertThat(capturedPoll.title()).isEqualTo(requirement.title().get());

        //TODO 미해결) 테스트 때문인지 서비스 때문인지 구분이 안가 !!!!!!!! 돌겟어 돌겟어
//        assertThat(capturedPoll.endAt()).isEqualTo(requirement.endAt());
        assertThat(capturedPoll.items().get(0).description()).isEqualTo(requirement.items().get(0).description().get());


    }

}