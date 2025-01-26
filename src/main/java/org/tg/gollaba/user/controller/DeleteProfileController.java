package org.tg.gollaba.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.service.DeleteProfileService;

@RestController
@RequestMapping("/v2/users/delete-profile")
@RequiredArgsConstructor
public class DeleteProfileController {

    private final DeleteProfileService service;

    @PreAuthorize("hasAnyAuthority('USER')")
    @DeleteMapping
    public ApiResponse<Void> delete(AuthenticatedUser user){
        service.delete(user.id());

        return ApiResponse.success();
    }
}
