package org.tg.gollaba.poll.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdController;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.service.GetTrendingPollsService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;

@RestController
@RequestMapping("/v2/polls/trending")
public class GetTrendingPollsController extends HashIdController {
    private final static int DEFAULT_LIMIT = 10;
    private final GetTrendingPollsService service;

    public GetTrendingPollsController(HashIdHandler hashIdHandler,
                                      GetTrendingPollsService service){
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<PollSummary>> get(Integer limit){
        limit = limit == null ? DEFAULT_LIMIT : limit;

        return ApiResponse.success(
            service.get(limit)
        );
    }

}
