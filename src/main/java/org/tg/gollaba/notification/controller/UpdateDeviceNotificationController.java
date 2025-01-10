package org.tg.gollaba.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.notification.service.UpdateDeviceNotificationService;
import org.tg.gollaba.common.web.ApiResponse;

@RestController
@RequestMapping("/v2/app-notifications")
@RequiredArgsConstructor
public class UpdateDeviceNotificationController {
    private final UpdateDeviceNotificationService service;

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping
    ApiResponse<Void> update(AuthenticatedUser user,
                             @Valid @RequestBody Request request) {
        var requirement = new UpdateDeviceNotificationService.Requirement(
            user.id(),
            request.agentId(),
            request.allowsNotification()
        );
        service.update(requirement);

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "agentId는 필수입니다.")
        String agentId,
        @NotNull(message = "allowsNotification는 필수입니다.")
        Boolean allowsNotification
    ) {
    }
}