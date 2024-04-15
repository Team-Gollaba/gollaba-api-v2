package org.tg.gollaba.poll.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdController;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.service.GetTop10PollsService;
import org.tg.gollaba.poll.vo.PollSummary;

@RestController
@RequestMapping("/v2/polls/total-top10")
public class GetTop10PollsController extends HashIdController {
    private final GetTop10PollsService service;

    public GetTop10PollsController(HashIdHandler hashIdHandler,
                                   GetTop10PollsService service){
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResponse<PollSummary>> get(Pageable pageable){
        return ApiResponse.success(
            PageResponse.from(service.get(pageable))
        );
    }
}
