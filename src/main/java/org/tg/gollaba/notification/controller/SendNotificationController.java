package org.tg.gollaba.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.notification.service.SendNotificationService;

@RestController
@RequestMapping("/v2/server-message")
@RequiredArgsConstructor
public class SendNotificationController {
    private final SendNotificationService notificationService;

    @PostMapping
    public ApiResponse<Void> sendServerMessage(@Valid @RequestBody Request request) {
        var requirement = new SendNotificationService.Requirement(
            request.title(),
            request.content()
        );
        notificationService.send(requirement);

        return ApiResponse.success();
    }

    public record Request(
        String title,
        @NotBlank(message = "알림 내용은 필수입니다.")
        String content
    ) {}
}