package org.tg.gollaba.poll.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.AdminUtilController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.component.HashIdHandler;

@RestController
@RequestMapping("/v2/hash")
public class GetHashIdController extends AdminUtilController {
    private final HashIdHandler hashIdHandler;

    public GetHashIdController(@Value("{security.admin-key}") String adminKey,
                               HashIdHandler hashIdHandler) {
        super(adminKey);
        this.hashIdHandler = hashIdHandler;
    }

    @GetMapping
    ApiResponse<String> get(@Valid @RequestBody Request request) {
        validate(request.adminKey());

        return ApiResponse.success(
            hashIdHandler.encode(request.pollId())
        );
    }

    record Request(
        @NotNull(message = "pollId는 필수입니다.")
        Long pollId,
        @NotBlank(message = "adminKey는 필수입니다.")
        String adminKey
    ) {
    }
}
