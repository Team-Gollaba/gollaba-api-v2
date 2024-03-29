package org.tg.gollaba.poll.component;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String uploadPollItemImage(long pollId,
                               long pollItemId,
                               MultipartFile multipartFile);
}
