package org.tg.gollaba.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class GetUserController {

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/me")
    ApiResponse<Void> getMe() {
        return ApiResponse.success();
    }
}
