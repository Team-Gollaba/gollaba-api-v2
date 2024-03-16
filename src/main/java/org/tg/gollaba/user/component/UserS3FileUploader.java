package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
        return s3Client.upload(
            s3Locations.profileImages().location(),
            "%d-%s".formatted(userId, UUID.randomUUID()),
            multipartFile
        );
    }

    @Override
    public String uploadBackgroundImage(long userId, MultipartFile multipartFile){
        return s3Client.upload(
            s3Locations.backgroundImages().location(),
            "%d-%s".formatted(userId, UUID.randomUUID()),
            multipartFile
        );
    }
}
