package org.tg.gollaba.user.component;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String uploadProfileImage(long userId, MultipartFile multipartFile);

    String uploadBackgroundImage(long userId, MultipartFile multipartFile);
}
