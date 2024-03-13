package org.tg.gollaba.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.service.UpdateUserService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class UpdateUserController {

    private final UpdateUserService service;

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping
    public ApiResponse<Void> update(AuthenticatedUser authenticatedUser,
                                    @Valid Request request){

       service.update(request.toRequirement(authenticatedUser.id()));

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "이름은 필수값입니다.")
        String name,
        Optional<MultipartFile> profileImage,
        Optional<MultipartFile> backgroundImage
    ){
        public UpdateUserService.Requirement toRequirement(Long userId) {
            return new UpdateUserService.Requirement(
                userId,
                name,
                profileImage,
                backgroundImage
            );
        }
    }
}