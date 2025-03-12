package org.tg.gollaba.poll.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.service.GetTopPollsService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;

@RestController
@RequestMapping("/v2/polls/top")
public class GetTopPollsController extends HashIdController {
    private final static int DEFAULT_LIMIT = 10;
    private final GetTopPollsService service;

    public GetTopPollsController(HashIdHandler hashIdHandler,
                                 GetTopPollsService service){
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    ApiResponse<List<PollSummaryResponse>> get(Integer limit){
        limit = limit == null ? DEFAULT_LIMIT : limit;
        var result = service.get(limit);
        return ApiResponse.success(
            convertToResponse(result)
        );
    }
}
