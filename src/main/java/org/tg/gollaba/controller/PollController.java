package org.tg.gollaba.controller;

import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.service.PollService;
import lombok.RequiredArgsConstructor;
import org.tg.gollaba.dto.PollDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
class PollController {
    private final PollService pollService;

    @PostMapping  //pollId는 여기서 못받음 전부 body로
    public ApiResponse<Long> create(@Validated @RequestBody CreateRequest request) {
        PollDto poll = pollService.create(request.toCreateRequirement());

        return ApiResponse.success(poll.id());

    }
    //createRequest

    //흐름
    //1. request 2개 선언한 것처럼 requirement도 2개 선언
    //2. pollOption request -> requirement 변환 메소드 작성
    //3. createRequest -> requirement 로 변환할 때, pollOption을 스트림해서 리스트로 넘기자 ...

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

            @NotNull(message = "투표 항목 설정은 필수입니다.")
            @Size(min = 2, max = 10, message = "투표 항목은 최소 2개에서 최대 10개까지 설정 가능합니다.")
            List<PollOptionRequest> pollOptions
    ){
        public PollService.CreateRequirement toCreateRequirement(){
            //list로 받은 request를 하나하나 requirement로 변경해주자 ...
            var pollOptionRequirements =
                    pollOptions.stream()
                    .map(PollOptionRequest::toPollOptionRequirement)
                    .collect(Collectors.toList());

            return new PollService.CreateRequirement(
                    userId,
                    title,
                    creatorName,
                    pollType,
                    responseType,
                    pollOptionRequirements //이러면 requirement로 전부 넘겨줄 수 있음
            );
        }
    }

    record PollOptionRequest(
            @NotBlank(message = "항목은 필수로 입력해 주어야 합니다")
            String description,
            String imageUrl
            //짜장면, 탕수율 이렇게 선택지 2개 만들고 싶으면 pollOption 객체 2개 생성?
    ){
        public PollService.PollOptionRequirement toPollOptionRequirement() {
            return new PollService.PollOptionRequirement(
                    description,
                    imageUrl
            );
        }
    }
}
