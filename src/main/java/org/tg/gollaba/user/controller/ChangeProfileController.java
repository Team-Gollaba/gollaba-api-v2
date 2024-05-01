package org.tg.gollaba.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.service.ChangeProfileService;


import static org.tg.gollaba.user.service.ChangeProfileService.*;

@RestController
@RequestMapping("/v2/users/change-profile")
@RequiredArgsConstructor
@Validated
public class ChangeProfileController {
    private final ChangeProfileService service;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ApiResponse<Void> upload(AuthenticatedUser user,
                                    @NotNull MultipartFile image){

        service.changeProfile(
            new Requirement(
                user.id(),
                image
            )
        );

        return ApiResponse.success(null);
    }
}