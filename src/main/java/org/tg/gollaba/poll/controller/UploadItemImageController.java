package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.service.UploadItemImageService;
import org.tg.gollaba.poll.service.UploadItemImageService.Requirement;

@RestController
@RequestMapping("/v2/polls/{pollHashId}/items/{itemId}/upload-image")
@RequiredArgsConstructor
public class UploadItemImageController {
    private final UploadItemImageService service;
    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ApiResponse<String> upload(AuthenticatedUser user,
                                           @PathVariable String pollHashId,
                                           @PathVariable Long itemId,
                                           MultipartFile image){
        var userId = user.id();
        var pollId = hashIdHandler.decode(pollHashId);

        return ApiResponse.success(
            service.upload(new Requirement(
                userId,
                pollId,
                itemId,
                image
            ))
        );
    }
}
