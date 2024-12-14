package org.tg.gollaba.image.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tg.gollaba.common.client.S3Client;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import static org.tg.gollaba.common.support.Status.INVALID_PARAMETER;

@Component
@RequiredArgsConstructor
public class S3FileUploader implements ImageUploader {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "heif", "heic");
    private final S3Client s3Client;

    @Override
    public String upload(String path, File file) {
        if (path == null || path.isBlank()) {
            throw new BadRequestException(INVALID_PARAMETER, "파일 경로가 비어있습니다.");
        }

        if (file == null || !file.exists()) {
            throw new BadRequestException(INVALID_PARAMETER, "파일이 존재하지 않습니다.");
        }

        var extension = StringUtils.getFilenameExtension(file.getName());

        if (!StringUtils.hasText(extension) || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException(Status.IMAGE_INVALID_FILE);
        }

        var fileName = UUID.randomUUID() + "." + extension;

        return s3Client.upload(path, fileName, file);
    }
}
