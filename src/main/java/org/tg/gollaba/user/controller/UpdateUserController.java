package org.tg.gollaba.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.service.UpdateUserService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/users/update")
@RequiredArgsConstructor
public class UpdateUserController {

    private final UpdateUserService service;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse<Void> update(@Valid @RequestBody Request request, AuthenticatedUser authenticatedUser){
       service.update(request.toRequirement() ,authenticatedUser);

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "name은 필수값입니다.")
        String name,
        Optional<MultipartFile> profileImage,
        Optional<MultipartFile> backgroundImage
    ){
        public UpdateUserService.Requirement toRequirement() {
            return new UpdateUserService.Requirement(
                name,
                profileImage,
                backgroundImage
            );
        }
    }

    public record Response(
        Long id
    ){}
}