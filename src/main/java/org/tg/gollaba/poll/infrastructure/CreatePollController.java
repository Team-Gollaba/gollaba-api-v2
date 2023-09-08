package org.tg.gollaba.poll.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.application.CreatePollService;
import org.tg.gollaba.poll.domain.Poll;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
public class CreatePollController {
    private final CreatePollService service;

    @PostMapping
    public ApiResponse<Response> create(@RequestBody @Valid Request request) {
        var pollId = service.create(request.toRequirement());

        return ApiResponse.success(
            new Response(pollId)
        );
    }

    record Request(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        @NotBlank(message = "작성자 이름을 입력해주세요.")
        String creatorName,
        @NotNull(message = "응답 유형을 선택해주세요.")
        Poll.PollResponseType responseType,
        @NotNull(message = "투표 유형을 선택해주세요.")
        Poll.PollType pollType,
        Optional<LocalDateTime> endedAt,
        @NotEmpty(message = "투표 항목을 입력해주세요.")
        List<Item> items
    ) {
        public CreatePollService.Requirement toRequirement() {

            return new CreatePollService.Requirement(
                null, //TODO: userId 추가
                title,
                creatorName,
                responseType,
                pollType,
                endedAt,
                items.stream()
                    .map(Item::toItem)
                    .toList()
            );
        }

        record Item(
            String description
        ) {
            public CreatePollService.Requirement.Item toItem() {
                return new CreatePollService.Requirement.Item(
                    description,
                    null
                );
            }
        }
    }

    record Response(
        Long pollId
    ) {
    }
}
