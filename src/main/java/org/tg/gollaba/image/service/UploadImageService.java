package org.tg.gollaba.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.image.component.ImageFileValidator;
import org.tg.gollaba.image.component.ImageUploader;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadImageService {
    private final ImageFileValidator imageFileValidator;
    private final ImageUploader imageUploader;

    public List<String> upload(Request request) {
        imageFileValidator.validateUpload(request.files());

        return request.files()
            .stream()
            .map(file -> imageUploader.upload(request.path(), file))
            .toList();
    }

    public record Request(
        String path,
        List<File> files
    ) {
    }
}
