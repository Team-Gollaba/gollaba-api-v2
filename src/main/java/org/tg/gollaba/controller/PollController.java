package org.tg.gollaba.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.service.PollService;
import lombok.RequiredArgsConstructor;
import org.tg.gollaba.dto.PollDto;

import java.util.Optional;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
class PollController {
    private final PollService pollService;

    @PostMapping  //pollId는 여기서 못받음 전부 body로
    public ApiResponse<Long> create(@RequestBody CreateRequest request) {
//        @PathVariable  Optional<Long> userId,
        PollDto poll = pollService.create(request.toCreateRequirement());

        return ApiResponse.success(poll.id());
    }

    record CreateRequest(
            Optional<Long> userId,

            @NotBlank(message = "제목을 입력해 주세요.")
            String title,
            @NotBlank(message = "작성자 이름을 입력해 주세요.")
            String creatorName,
            @NotNull(message = "익명투표, 기명투표 선택해 주세요.")
            Poll.PollType pollType,
            @NotNull(message = "단일투표, 중복투표를 선택해 주세요.")
            Poll.PollResponseType responseType
    ){
        public PollService.CreateRequirement toCreateRequirement(){
            return new PollService.CreateRequirement(
                    userId,
                    title,
                    creatorName,
                    pollType,
                    responseType);
        }
    }
    record List<PollOption>(
            @NotBlank(message = "항목은 필수로 입력해 주어야 합니다")
            String description,
            Optional<String> imageUrl
            //짜장면, 탕수율 이렇게 선택지 2개 만들고 싶으면 pollOption 객체 2개 생성?
    ){}
}
