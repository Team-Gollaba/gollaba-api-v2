package org.tg.gollaba.poll.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.AdminUtilController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;

import java.util.Optional;

@RestController
@RequestMapping("/v2/hash")
public class GetHashIdController extends AdminUtilController {
    private final HashIdHandler hashIdHandler;

    public GetHashIdController(@Value("${security.admin-key}") String adminKey,
                               HashIdHandler hashIdHandler) {
        super(adminKey);
        this.hashIdHandler = hashIdHandler;
    }

    @GetMapping
    ApiResponse<Object> get(@Valid Request request) {
        validate(request.adminKey());

        if (request.pollId().isPresent()) {
            return ApiResponse.success(hashIdHandler.encode(request.pollId().get()));
        }

        if (request.pollHashId().isPresent()) {
            return ApiResponse.success(hashIdHandler.decode(request.pollHashId().get()));
        }

        throw new BadRequestException(Status.INVALID_PARAMETER);
    }

    record Request(
        Optional<Long> pollId,
        Optional<String> pollHashId,
        @NotBlank(message = "adminKey는 필수입니다.")
        String adminKey
    ) {
    }
}
