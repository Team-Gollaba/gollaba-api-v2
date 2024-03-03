package org.tg.gollaba.user.component;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String uploadProfileImage(String fileName,
                              MultipartFile multipartFile);
}
