package org.tg.gollaba.poll.component;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String upload(MultipartFile multipartFile, String dirName);

}
