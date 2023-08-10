package org.tg.gollaba.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.service.PollService;
import lombok.RequiredArgsConstructor;
import org.tg.gollaba.vo.PollVo;
import java.util.Optional;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
class PollController {
    private final PollService pollService;

    @PostMapping ("/{userId}")
    public ApiResponse<Long> create(@PathVariable Optional<Long> userId,
                                    @RequestBody Request request) {
//        @PathVariable  Optional<Long> userId,
        PollVo poll = pollService.create(request.toCreateRequest(userId));


        return ApiResponse.success(poll.id());
    }

    record Request(
            @NotBlank(message = "제목을 입력해 주세요.")
            String title,
            @NotBlank(message = "작성자 이름을 입력해 주세요.")
            String creatorName,
            Poll.PollType pollType,
            Poll.PollResponseType responseType
    ){
        public PollService.CreateRequest toCreateRequest(Optional<Long> userId){
            return new PollService.CreateRequest(
                    userId,
                    title,
                    creatorName,
                    pollType,
                    responseType);
        }
    }
}
