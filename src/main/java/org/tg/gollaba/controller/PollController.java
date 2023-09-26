package org.tg.gollaba.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.service.PollService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
class PollController {
    private final PollService pollService;

    @PostMapping
    public ApiResponse<CreatePollResponse> create(@Valid @RequestBody CreateRequest request) {
        var pollId = pollService.create(request.toCreateRequirement());

        return ApiResponse.success(
            new CreatePollResponse(pollId)
        );
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
            Poll.PollResponseType responseType,

            @NotEmpty(message = "투표 항목 설정은 필수입니다.")
            @Size(min = 2, max = 10, message = "투표 항목은 최소 2개에서 최대 10개까지 설정 가능합니다.")
            List<PollItemRequest> pollItems,

            Optional<LocalDateTime> endedAt
    ){
        private PollService.CreateRequirement toCreateRequirement(){
            var pollItemRequirements =
                pollItems.stream()
                        .map(pollItemRequest -> new PollService.CreateRequirement.PollItemRequirement(
                            pollItemRequest.description,
                            pollItemRequest.imageUrl
                        ))
                    .collect(Collectors.toList());

            return new PollService.CreateRequirement(
                    userId,
                    title,
                    creatorName,
                    pollType,
                    responseType,
                    pollItemRequirements,
                    endedAt
            );
        }

        record PollItemRequest(
            @NotBlank(message = "항목은 필수로 입력해 주어야 합니다")
            String description,
            String imageUrl
        ){}
    }
    record CreatePollResponse(
        Long pollId
    ){}
}
