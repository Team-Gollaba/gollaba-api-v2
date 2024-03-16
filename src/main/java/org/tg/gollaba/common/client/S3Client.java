package org.tg.gollaba.common.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.ServerException;

import java.io.InputStream;

import static org.springframework.util.StringUtils.getFilenameExtension;
import static org.tg.gollaba.common.support.Status.FAIL_TO_UPLOAD;

@Component
@Slf4j
public class S3Client {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3Client(AmazonS3 amazonS3,
                    @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public String upload(String filePath,
                         String fileName,
                         MultipartFile multipartFile) {
        var extension = getFilenameExtension(multipartFile.getOriginalFilename());
        var objMeta = new ObjectMetadata();
        InputStream inputStream;
        int contentLength;

        try {
            inputStream = multipartFile.getInputStream();
            contentLength = inputStream.available();
        } catch (Exception e) {
            log.error("Failed to upload file to S3", e);
            throw new ServerException(FAIL_TO_UPLOAD);
        }

        objMeta.setContentLength(contentLength);
        amazonS3.putObject(
            bucket,
            filePath + "/" + fileName + "." + extension,
            inputStream,
            objMeta
        );

        return amazonS3.getUrl(bucket, filePath + "/" + fileName).toString();
    }
}
