package org.tg.gollaba.image.controller;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public abstract class AbstractUploadFileController {
    protected File fileFrom(MultipartFile multipartFile) {
        try {
            var tempFile = File.createTempFile(
                "uploaded-file-",
                multipartFile.getOriginalFilename()
            );

            tempFile.deleteOnExit();
            multipartFile.transferTo(tempFile);

            return tempFile;
        } catch (IOException e) {
            throw new MultipartFileSavingException(e);
        }
    }

    static class MultipartFileSavingException extends RuntimeException {
        MultipartFileSavingException(Throwable e) {
            super(e);
        }
    }
}
