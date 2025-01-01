package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.service.GetPollSearchedHistoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/polls/search-trending")
@RequiredArgsConstructor
public class GetPollSearchedHistoryController {
    private final GetPollSearchedHistoryService service;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> get(){
        return ApiResponse.success(
            service.get()
        );
    }
}