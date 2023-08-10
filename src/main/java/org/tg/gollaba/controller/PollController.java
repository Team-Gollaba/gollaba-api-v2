package org.tg.gollaba.controller;

import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.service.PollService;
import org.tg.gollaba.vo.PollVo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/polls")
@RequiredArgsConstructor
public class PollController {
    private final PollService pollService;

    @PostMapping //TODO: 구현하기
    public ApiResponse<Long> create() {
        return null;
    }
}
