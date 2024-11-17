package org.tg.gollaba.poll.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.client.S3Client;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.config.S3Locations;

import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PollS3FileUploader implements FileUploader {
    private final S3Client s3Client;
    private final S3Locations s3Locations;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "heif", "heic");

    @Override
    public String uploadPollItemImage(long pollId,
                                      long pollItemId,
                                      MultipartFile multipartFile) {
        var filePath = s3Locations.pollItems().location();
        var originalFileName = multipartFile.getOriginalFilename();

        var extension = StringUtils.getFilenameExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException(Status.IMAGE_INVALID_FILE);
        }

        var fileName = "%d-%d-%s".formatted(pollId, pollItemId, UUID.randomUUID());
        var imageUrl = s3Client.upload(filePath, fileName, multipartFile);

        return imageUrl + "." + extension;
    }
}
