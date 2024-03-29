package org.tg.gollaba.poll.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.component.HashIdHandler;
import org.tg.gollaba.poll.controller.aspect.UseHashId;
import org.tg.gollaba.poll.service.GetPollDetailsService;

import java.util.Map;

@RestController
@RequestMapping("/v2/polls/{pollId}")
@RequiredArgsConstructor
public class GetPollDetailsController {
    private final GetPollDetailsService service;
    private final ObjectMapper objectMapper;
    private final HashIdHandler hashIdHandler;

    @UseHashId
    @GetMapping
    ApiResponse<Map<String, Object>> get(@PathVariable Object pollId) {
        if (!(pollId instanceof Long id)) {
            throw new BadRequestException(Status.INVALID_PARAMETER, "잘못된 pollId 입니다.");
        }

        var pollDetails = service.get(id);
        Map<String, Object> response = objectMapper.convertValue(pollDetails, Map.class);
        response.put("id", hashIdHandler.encode(id));

        return ApiResponse.success(response);
    }
}
