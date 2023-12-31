package org.tg.gollaba.poll.application;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.application.CreatePollService.Requirement.Item;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.infrastructure.S3Uploader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreatePollServiceTest {
    @InjectMocks
    private CreatePollService service;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Test
    void create() {
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var requirement = new RequirementFixture()
            .setItems(List.of(
                new Item("description1", Optional.ofNullable(null)),
                new Item("description2", Optional.ofNullable(mockImageFile))
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
        private Optional<LocalDateTime> endedAt = Optional.empty();
        private List<Item> items = List.of(
            new Item("description1", null),
            new Item("description2", null)
        );

        @Override
        public CreatePollService.Requirement build() {
            return new CreatePollService.Requirement(
                userId,
                title,
                creatorName,
                responseType,
                pollType,
                endedAt,
                items
            );
        }
        private MultipartFile mockMultipartFile(String fileName) {
            MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
            return mockMultipartFile;
        }
    }
}