package org.tg.gollaba.voting.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.voting.service.GetMyVotingService;

import java.util.List;

@RestController
@RequestMapping("/v2/voting/me")
@RequiredArgsConstructor
public class GetMyVotingController {
    private final GetMyVotingService service;
    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping
    ApiResponse<Response> get(@RequestBody @Valid Request request,
                              AuthenticatedUser user) {
        var pollId = hashIdHandler.decode(request.pollHashId());
        var voting = service.getMyVoting(pollId, user.id());

        return ApiResponse.success(
            new Response(voting.id(),voting.votedItemIds())
        );
    }

    record Request(
        @NotBlank(message = "pollHashId 는 필수 값입니다.")
        String pollHashId
    ) {
    }

    record Response(
        Long id,
        List<Long> votedItemIds
    ) {
    }
}
