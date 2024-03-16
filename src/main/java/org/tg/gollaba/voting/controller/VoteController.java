package org.tg.gollaba.voting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.service.VoteService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/voting")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService service;

    @PostMapping(headers = HttpHeaders.AUTHORIZATION)
    public ApiResponse<Void> create(AuthenticatedUser user,
                                    @RequestBody @Valid Request request,
                                    HttpServletRequest httpServletRequest) {
        var requirement = createRequirement(
            request,
            httpServletRequest
        );

        service.vote(requirement);

        return ApiResponse.success();
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody @Valid Request request,
                                    HttpServletRequest httpServletRequest) {
        var requirement = createRequirement(
            request,
            httpServletRequest
        );

        service.vote(requirement);

        return ApiResponse.success();
    }

    private VoteService.Requirement createRequirement(Request request,
                                                      HttpServletRequest httpServletRequest) {
        return new VoteService.Requirement(
            request.pollId(),
            request.pollItemIds(),
            "", // TODO: IP 주소 추가
            request.userId(),
            request.voterName()
        );
    }

    record Request(
        @NotNull(message = "투표 ID는 필수입니다.")
        Long pollId,
        @NotEmpty(message = "투표 항목 ID는 필수입니다.")
        Set<Long> pollItemIds,
        Optional<Long> userId, //TODO: user 인증 생기면 삭제
        Optional<String> voterName
    ) {
    }
}
