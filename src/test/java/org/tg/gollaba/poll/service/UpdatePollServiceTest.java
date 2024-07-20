package org.tg.gollaba.poll.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.domain.PollItemFixture;
import org.tg.gollaba.poll.repository.PollRepository;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UpdatePollServiceTest {
    @InjectMocks
    private UpdatePollService service;
    @Mock
    private PollRepository pollRepository;

    @Test
    void success(){
        //given
        var poll = new PollFixture()
            .setItems(List.of(
                new PollItemFixture().setId(1L).build(),
                new PollItemFixture().setId(2L).build()
            ))
            .build();

        var requirement = new RequirementFixture().build();
        given(pollRepository.findMyPoll(requirement.pollId(), requirement.userId()))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(() -> service.update(requirement));

        //then
        assertThat(throwable).isNull();
        verify(pollRepository, times(1)).save(any(Poll.class));

        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(pollRepository).save(argumentCaptor.capture());

        var capturedPoll = argumentCaptor.getValue();
        assertThat(capturedPoll.title()).isEqualTo(requirement.title());
        assertThat(capturedPoll.endAt()).isEqualTo(requirement.endAt());
        assertThat(capturedPoll.items().get(0).description())
            .isEqualTo(requirement.items().get(0).description());
        assertThat(capturedPoll.items().get(0).imageUrl())
            .isEqualTo(requirement.items().get(0).image());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RequirementFixture implements TestFixture<UpdatePollService.Requirement> {
        private Long userId = 1L;
        private Long pollId = 1L;
        private String title = "updateTitle";
        private LocalDateTime endAt = LocalDateTime.now().plusMinutes(60);
        List<UpdatePollService.Requirement.Item> updatedPollItems = List.of(
            new UpdatePollService.Requirement.Item("updatedDescription1", "https://update-poll-item.com/imageA.jpg"),
            new UpdatePollService.Requirement.Item("updatedDescription2", "https://update-poll-item.com/imageB.jpg")
        );

        @Override
        public UpdatePollService.Requirement build() {
            return new UpdatePollService.Requirement(
                userId,
                pollId,
                title,
                endAt,
                updatedPollItems
            );
        }
    }
}