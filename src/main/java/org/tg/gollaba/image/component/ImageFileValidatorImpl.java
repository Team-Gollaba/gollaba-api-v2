package org.tg.gollaba.image.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageFileValidatorImpl implements ImageFileValidator {
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".svg"
    );
    private final Tika tika;

    public void validateUpload(File file) {
        checkExists(file);
        checkImageFile(file);
    }

    public void validateUpload(List<File> files) {
        files.forEach(file -> {
            checkExists(file);
            checkImageFile(file);
        });
    }

    private void checkExists(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new InvalidImageFileException("파일이 존재하지 않습니다.");
        }
    }

    private void checkImageFile(File file) {
        // MIME 타입 확인
        String mimeType = null;

        try {
            mimeType = tika.detect(file);
        } catch (IOException e) {
            throw new InvalidImageFileException("파일의 MIME 타입을 감지할 수 없습니다.");
        }

        if (!mimeType.startsWith("image/")) {
            throw new InvalidImageFileException("이미지 파일이 아닙니다.");
        }

        // 파일 확장자 확인
        var fileName = file.getName().toLowerCase();
        ALLOWED_IMAGE_EXTENSIONS.stream()
            .filter(fileName::endsWith)
            .findAny()
            .orElseThrow(() -> new InvalidImageFileException("지원하지 않는 이미지 파일입니다.\n파일 이름: " + fileName));
    }
}
