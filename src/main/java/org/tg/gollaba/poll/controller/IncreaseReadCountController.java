package org.tg.gollaba.poll.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.service.IncreaseReadCountService;

@RestController
@RequestMapping("/v2/polls/{pollHashId}/read")
public class IncreaseReadCountController extends HashIdController {
    private final IncreaseReadCountService service;

    public IncreaseReadCountController(HashIdHandler hashIdHandler,
                                       IncreaseReadCountService service) {
        super(hashIdHandler);
        this.service = service;
    }

    @PostMapping
    ApiResponse<Void> get(@PathVariable String pollHashId) {
        var id = getPollId(pollHashId);

        service.increase(id);

        return ApiResponse.success();
    }
}