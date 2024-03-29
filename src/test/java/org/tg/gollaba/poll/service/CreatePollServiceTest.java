package org.tg.gollaba.poll.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.component.FileUploader;
import org.tg.gollaba.poll.service.CreatePollService.Requirement.Item;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePollServiceTest {
    @InjectMocks
    private CreatePollService service;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private FileUploader fileUploader;

    @Test
    void success() {
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var requirement = new RequirementFixture()
            .setItems(List.of(
                new Item("description1", Optional.ofNullable(mockImageFile)),
                new Item("description2", Optional.ofNullable(mockImageFile))
                ))
            .build();
        var poll = new PollFixture().build();
        given(pollRepository.save(any(Poll.class)))
            .willReturn(poll);
        given(fileUploader.uploadPollItemImage(anyLong(), anyLong(), any()))
            .willReturn("https://example.com/image.jpg");

        //when
        var throwable = catchThrowable(() -> service.create(requirement));

        //then
        assertThat(throwable).isNull();
        verify(pollRepository, times(1)).save(any(Poll.class));
        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(pollRepository).save(argumentCaptor.capture());
        var savedPoll = argumentCaptor.getValue();
        assertThat(savedPoll.userId()).isEqualTo(requirement.userId().orElseThrow());
        assertThat(savedPoll.title()).isEqualTo(requirement.title());
        assertThat(savedPoll.creatorName()).isEqualTo(requirement.creatorName());
        assertThat(savedPoll.responseType()).isEqualTo(requirement.responseType());
        assertThat(savedPoll.pollType()).isEqualTo(requirement.pollType());
        assertThat(savedPoll.endAt()).isEqualTo(requirement.endAt().orElseThrow());
        assertThat(savedPoll.items().size()).isEqualTo(2);
        assertThat(savedPoll.items().get(0).description()).isEqualTo(requirement.items().get(0).description());
        assertThat(savedPoll.items().get(1).description()).isEqualTo(requirement.items().get(1).description());
    }

    @Test
    void 이미지_파일이_없으면_건너뛴다() {
        //given
        var requirement = new RequirementFixture()
            .setItems(List.of(
                new Item("description1", Optional.empty()),
                new Item("description2", Optional.empty())
            ))
            .build();
        var poll = new PollFixture().build();
        given(pollRepository.save(any(Poll.class)))
            .willReturn(poll);

        //when
        var throwable = catchThrowable(() -> service.create(requirement));

        //then
        assertThat(throwable).isNull();
        verify(pollRepository, times(1)).save(any(Poll.class));
        verify(fileUploader, never()).uploadPollItemImage(anyLong(), anyLong(), any());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class RequirementFixture implements TestFixture<CreatePollService.Requirement> {
        private Long userId = 1L;
        private String title = "title";
        private String creatorName = "creatorName";
        private Poll.PollResponseType responseType = Poll.PollResponseType.SINGLE;
        private Poll.PollType pollType = Poll.PollType.ANONYMOUS;
        private Optional<LocalDateTime> endAt = Optional.of(LocalDateTime.now().plusWeeks(2));
        private List<Item> items = List.of(
            new Item("description1", null),
            new Item("description2", null)
        );

        @Override
        public CreatePollService.Requirement build() {
            return new CreatePollService.Requirement(
                Optional.of(userId),
                title,
                creatorName,
                responseType,
                pollType,
                endAt,
                items
            );
        }
    }
}