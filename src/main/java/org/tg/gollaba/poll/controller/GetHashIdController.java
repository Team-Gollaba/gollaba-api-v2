package org.tg.gollaba.poll.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.component.HashIdHandler;

import static org.tg.gollaba.common.support.Status.INVALID_PARAMETER;

@RestController
@RequestMapping("/v2/hash")
@RequiredArgsConstructor
public class GetHashIdController {
    private final HashIdHandler hashIdHandler;
    @Value("${security.hash-ids.admin-key}")
    private String adminKey;

    /**
     * hashId 추출용 api
     * (어드민 전용)
     */
    @GetMapping
    ApiResponse<String> get(Request request) {
        validate(request.adminKey());

        return ApiResponse.success(
            hashIdHandler.encode(request.pollId())
        );
    }

    public void validate(String requestAdminKey) {
        if (!adminKey.equals(requestAdminKey)) {
            throw new BadRequestException(INVALID_PARAMETER, "잘못된 adminKey 입니다.");
        }
    }

    record Request(
        @NotNull(message = "pollId는 필수입니다.")
        Long pollId,
        @NotBlank(message = "adminKey는 필수입니다.")
        String adminKey
    ) {
    }
}
