package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.client.S3Client;
import org.tg.gollaba.config.S3Locations;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserS3FileUploader implements FileUploader {
    private final S3Client s3Client;
    private final S3Locations s3Locations;

    @Override
    public String uploadProfileImage(long userId, MultipartFile multipartFile) {
        var originalFileName = multipartFile.getOriginalFilename();
        var extension = StringUtils.getFilenameExtension(originalFileName);

        var imageUrl = s3Client.upload(
            s3Locations.profileImages().location(),
            "%d-%s".formatted(userId, UUID.randomUUID()),
            multipartFile
        );

        return imageUrl + "." + extension;
    }
}
