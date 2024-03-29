package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.controller.aspect.UseHashId;
import org.tg.gollaba.poll.service.IncreaseReadCountService;

@RestController
@RequestMapping("/v2/polls/{pollId}/read")
@RequiredArgsConstructor
public class IncreaseReadCountController {
    private final IncreaseReadCountService service;

    @UseHashId
    @PostMapping
    ApiResponse<Void> get(@PathVariable Object pollId) {
        if (!(pollId instanceof Long id)) {
            throw new BadRequestException(Status.INVALID_PARAMETER, "잘못된 pollId 입니다.");
        }

        service.increase(id);

        return ApiResponse.success();
    }
}