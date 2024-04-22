package org.tg.gollaba.poll.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdController;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.service.UpdatePollService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/v2/polls/{pollHashId}")
public class UpdatePollController extends HashIdController{
    private final UpdatePollService service;

    public UpdatePollController(HashIdHandler hashIdHandler,
                                UpdatePollService service){
    super(hashIdHandler);
    this.service = service;
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping
    public ApiResponse<Void> update(AuthenticatedUser user,
                                    @PathVariable String pollHashId,
                                    @Valid @RequestBody Request request){
        service.update(
            convert(
                user.id(),
                request,
                getPollId(pollHashId)
            )
        );

        return ApiResponse.success(null);
    }

    private static UpdatePollService.Requirement convert(Long userId,
                                                         Request request,
                                                         Long pollHashId){
        var requirementPollItems = request.items.stream()
            .map(pollItem -> new UpdatePollService.Requirement.Item(
                pollItem.description,
                pollItem.imageUrl
            ))
            .collect(toList());

        return new UpdatePollService.Requirement(
            userId,
            pollHashId,
            request.title,
            request.endAt,
            requirementPollItems
        );
    }

    public record Request(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @NotNull(message = "투표 유효기간은 설정해주세요.")
        LocalDateTime endAt,

        @NotEmpty(message = "투표 항목을 입력해주세요.")
        @Valid
        List<Item> items
    ) {
        public record Item(
            @NotBlank(message = "투표 항목은 비어있을 수 없습니다.")
            String description,

            @NotBlank(message = "이미지 링크를 삽입해주세요.")
            String imageUrl
        ) {
        }
    }
}