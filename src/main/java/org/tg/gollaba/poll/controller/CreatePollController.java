package org.tg.gollaba.poll.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.poll.service.CreatePollService;
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
    public ApiResponse<Response> create(@Valid Request request) {
        var pollId = service.create(request.toRequirement());

        return ApiResponse.success(
            new Response(pollId)
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class Request {
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;
        @NotBlank(message = "작성자 이름을 입력해주세요.")
        private String creatorName;
        @NotNull(message = "응답 유형을 선택해주세요.")
        private Poll.PollResponseType responseType;
        @NotNull(message = "투표 유형을 선택해주세요.")
        private Poll.PollType pollType;
        private LocalDateTime endedAt;
        @NotEmpty(message = "투표 항목을 입력해주세요.")
        @Valid
        private List<Item> items;

        public CreatePollService.Requirement toRequirement() {

            return new CreatePollService.Requirement(
                null, //TODO: userId 추가
                title,
                creatorName,
                responseType,
                pollType,
                Optional.ofNullable(endedAt),
                items.stream()
                    .map(Item::toItem)
                    .toList()
            );
        }

        @Getter
        @Setter
        @NoArgsConstructor
        static class Item {
            @NotBlank(message = "투표 항목은 비어있을 수 없습니다.")
            private String description;
            private MultipartFile image;

            public CreatePollService.Requirement.Item toItem() {
                return new CreatePollService.Requirement.Item(
                    description,
                    Optional.ofNullable(image)
                );
            }
        }
    }

    record Response(
        Long pollId
    ) {
    }
}
