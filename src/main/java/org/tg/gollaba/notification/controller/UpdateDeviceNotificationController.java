package org.tg.gollaba.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.notification.service.UpdateDeviceNotificationService;
import org.tg.gollaba.common.web.ApiResponse;

@RestController
@RequestMapping("/v2/app-notifications")
@RequiredArgsConstructor
public class UpdateDeviceNotificationController {
    private final UpdateDeviceNotificationService service;

    @PutMapping
    ApiResponse<Void> update(@Valid @RequestBody Request request) {
        var requirement = new UpdateDeviceNotificationService.Command(
            request.userId(),
            request.agentId(),
            request.allowsNotification()
        );
        service.update(requirement);

        return ApiResponse.success();
    }

    record Request(
        @NotNull(message = "userId는 필수입니다.")
        Long userId,
        @NotBlank(message = "agentId는 필수입니다.")
        String agentId,
        @NotNull(message = "allowsNotification는 필수입니다.")
        Boolean allowsNotification
    ) {
    }
}