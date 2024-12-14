package org.tg.gollaba.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.image.service.UploadImageService;

import java.util.List;

@RestController
@RequestMapping("/v2/image/upload")
@RequiredArgsConstructor
public class UploadImageController extends AbstractUploadFileController {
    private final UploadImageService service;

    @PostMapping
    ApiResponse<List<String>> upload(@RequestPart("filePath") String filePath,
                                     @RequestPart("files") List<MultipartFile> multipartFiles) {
        var files = multipartFiles.stream()
            .map(this::fileFrom)
            .toList();

        var request = new UploadImageService.Request(filePath, files);

        return ApiResponse.success(
            service.upload(request)
        );
    }
}
