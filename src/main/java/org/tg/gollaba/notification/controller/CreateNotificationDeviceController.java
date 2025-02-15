package org.tg.gollaba.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.notification.domain.NotificationDevice;
import org.tg.gollaba.notification.service.CreateNotificationDeviceService;

@RestController
@RequestMapping("/v2/app-notifications")
@RequiredArgsConstructor
public class CreateNotificationDeviceController {
    private final CreateNotificationDeviceService service;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    ApiResponse<Void> create(AuthenticatedUser user,
                             @Valid @RequestBody Request request) {
        var requirement = new CreateNotificationDeviceService.Requirement(
            user.id(),
            request.agentId(),
            request.osType(),
            request.deviceName(),
            request.allowsNotification()
        );
        service.create(requirement);

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "agentId는 필수입니다.")
        String agentId,
        @NotNull(message = "osType은 필수입니다.")
        NotificationDevice.OperatingSystemType osType,
        @NotBlank(message = "deviceName은 필수입니다.")
        String deviceName,
        @NotNull(message = "allowsNotification는 필수입니다.")
        Boolean allowsNotification
    ) {
    }
}