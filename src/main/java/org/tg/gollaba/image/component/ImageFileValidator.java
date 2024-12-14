package org.tg.gollaba.image.component;

import org.tg.gollaba.common.exception.BadRequestException;

import java.io.File;
import java.util.List;

import static org.tg.gollaba.common.support.Status.INVALID_PARAMETER;

public interface ImageFileValidator {

    void validateUpload(File file);

    void validateUpload(List<File> files);

    class InvalidImageFileException extends BadRequestException {
        public InvalidImageFileException(String message) {
            super(INVALID_PARAMETER, message);
        }
    }
}
