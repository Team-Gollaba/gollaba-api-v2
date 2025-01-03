package org.tg.gollaba.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.notification.domain.DeviceNotification;
import org.tg.gollaba.notification.service.CreateDeviceNotificationService;

@RestController
@RequestMapping("/v2/app-notifications")
@RequiredArgsConstructor
public class CreateDeviceNotificationController {
    private final CreateDeviceNotificationService service;

    @PostMapping
    ApiResponse<Void> create(@Valid @RequestBody Request request) {
        var command = new CreateDeviceNotificationService.Command(
            request.userId(),
            request.agentId(),
            request.osType(),
            request.deviceName(),
            request.allowsNotification()
        );
        service.create(command);

        return ApiResponse.success();
    }

    record Request(
        @NotNull(message = "userId는 필수입니다.")
        Long userId,
        @NotBlank(message = "agentId는 필수입니다.")
        String agentId,
        @NotNull(message = "osType은 필수입니다.")
        DeviceNotification.OperatingSystemType osType,
        @NotBlank(message = "deviceName은 필수입니다.")
        String deviceName,
        @NotNull(message = "allowsNotification는 필수입니다.")
        Boolean allowsNotification
    ) {
    }
}