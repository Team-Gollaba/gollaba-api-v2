package org.tg.gollaba.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.service.GetSelfService;

@RestController
@RequestMapping("/v2/users/me")
@RequiredArgsConstructor
public class GetSelfController {
    private final GetSelfService service;

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping
    ApiResponse<GetSelfService.UserDetails> getMe(AuthenticatedUser user) {
        var userDetails = service.get(user.id());

        return ApiResponse.success(
            new GetSelfService.UserDetails(
                userDetails.name(),
                userDetails.email(),
                userDetails.roleType(),
                userDetails.providerType(),
                userDetails.profileImageUrl(),
                userDetails.backgroundImageUrl()
            )
        );
    }
}
