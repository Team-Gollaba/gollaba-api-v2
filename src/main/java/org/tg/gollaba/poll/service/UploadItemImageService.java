package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.component.FileUploader;
import org.tg.gollaba.poll.repository.PollRepository;

@Service
@RequiredArgsConstructor
public class UploadItemImageService {
    private final PollRepository pollRepository;
    private final FileUploader fileUploader;

    @Transactional
    public String upload(Requirement requirement){
        var poll = pollRepository.findMyPoll(requirement.pollId(), requirement.userId())
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));

        var item = poll.getItem(requirement.itemId());
        var imageUrl = fileUploader.uploadPollItemImage(
            poll.id(),
            item.id(),
            requirement.image()
        );

        return imageUrl;
    }

    public record Requirement(
        Long userId,
        Long pollId,
        Long itemId,
        MultipartFile image
    ){
    }
}
