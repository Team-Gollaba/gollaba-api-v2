package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.component.PollS3FileUploader;
import org.tg.gollaba.poll.repository.PollRepository;

@Service
@RequiredArgsConstructor
public class UpdateItemImageService {
    private final PollRepository pollRepository;
    private final PollS3FileUploader pollS3FileUploader;

    @Transactional
    public ImageUrl imageUpload(Long pollHashId,
                                Long itemId,
                                MultipartFile image){
        var poll = pollRepository.findById(pollHashId)
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));
        var item = poll.getItem(itemId);
        var imageUrl = pollS3FileUploader.uploadPollItemImage(
                poll.id(),
                item.id(),
                image
            );

        return new ImageUrl(imageUrl);
    }

    public record ImageUrl(
        String url
    ){}
}
