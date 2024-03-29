package org.tg.gollaba.poll.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.client.S3Client;
import org.tg.gollaba.config.S3Locations;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PollS3FileUploader implements FileUploader {
    private final S3Client s3Client;
    private final S3Locations s3Locations;

    @Override
    public String uploadPollItemImage(long pollId,
                                      long pollItemId,
                                      MultipartFile multipartFile) {
        var filePath = s3Locations.pollItems().location();
        var fileName = "%d-%d-%s".formatted(pollId, pollItemId, UUID.randomUUID());

        return s3Client.upload(filePath, fileName, multipartFile);
    }
}
