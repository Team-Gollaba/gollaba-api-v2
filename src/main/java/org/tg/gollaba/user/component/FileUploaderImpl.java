package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.component.S3FileUploader;
import org.tg.gollaba.config.S3Locations;

@Component
@RequiredArgsConstructor
public class FileUploaderImpl implements FileUploader {
    private final S3FileUploader s3FileUploader;
    private final S3Locations s3Locations;

    @Override
    public String uploadProfileImage(String fileName,
                                     MultipartFile multipartFile) {
        return s3FileUploader.upload(
            s3Locations.profileImages().location(),
            fileName,
            multipartFile
        );
    }

    @Override
    public String uploadBackgroundImage(String fileName,
                                        MultipartFile multipartFile){
        return s3FileUploader.upload(
            s3Locations.backgroundImages().location(),
            fileName,
            multipartFile
        );
    }
}
