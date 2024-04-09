package org.tg.gollaba.poll.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.component.PollS3FileUploader;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateItemImageServiceTest {

    @InjectMocks
    private UpdateItemImageService service;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollS3FileUploader pollS3FileUploader;

    @Mock
    private HashIdHandler hashIdHandler;

    @Test
    void success(){
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var poll = new PollFixture().build();
        var pollId = 1L;
        var itemId = 1L;
        given(pollRepository.findById(pollId))
            .willReturn(Optional.of(poll));

        //when
        var throwable = Assertions.catchThrowable(() -> service.imageUpload(poll.id(), itemId, mockImageFile));

        //then
        assertThat(throwable).isNull();
        verify(pollS3FileUploader, times(1)).uploadPollItemImage(poll.id(), itemId, mockImageFile);
    }

}