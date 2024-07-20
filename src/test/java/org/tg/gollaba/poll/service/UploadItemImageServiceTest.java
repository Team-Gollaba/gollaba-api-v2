package org.tg.gollaba.poll.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.component.FileUploader;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadItemImageServiceTest{

    @InjectMocks
    private UploadItemImageService service;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private FileUploader fileUploader;

    @Test
    void success(){
        //given
        var requirement = new RequirementFixture().build();
        var poll = new PollFixture().build();
        given(pollRepository.findMyPoll(requirement.pollId(), requirement.userId()))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(() -> service.upload(requirement));

        //then
        assertThat(throwable).isNull();
        verify(fileUploader, times(1)).uploadPollItemImage(
            requirement.pollId(),
            requirement.itemId(),
            requirement.image()
        );
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RequirementFixture implements TestFixture<UploadItemImageService.Requirement> {
        private Long userId = 1L;
        private Long pollId = 1L;
        private Long itemId = 1L;
        private MultipartFile mockImageFile = Mockito.mock(MultipartFile.class);

        @Override
        public UploadItemImageService.Requirement build() {
            return new UploadItemImageService.Requirement(
                userId,
                pollId,
                itemId,
                mockImageFile
            );
        }
    }
}