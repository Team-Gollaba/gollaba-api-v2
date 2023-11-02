package org.tg.gollaba.poll.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploader {

    String upload(MultipartFile multipartFile, String dirName) throws IOException;

}
