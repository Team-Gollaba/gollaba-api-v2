package org.tg.gollaba.voting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.support.IpAddressExtractor;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.voting.service.VoteService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/voting")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService service;
    private final HashIdHandler hashIdHandler;

    @PostMapping(headers = HttpHeaders.AUTHORIZATION)
    public ApiResponse<Void> create(AuthenticatedUser user,
                                    @RequestBody @Valid Request request,
                                    HttpServletRequest httpServletRequest) {
        var pollId = hashIdHandler.decode(request.pollHashId());
        var requirement = new VoteService.Requirement(
            pollId,
            request.pollItemIds(),
            IpAddressExtractor.extract(httpServletRequest),
            Optional.of(user.id()),
            request.voterName()
        );

        service.vote(requirement);

        return ApiResponse.success();
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody @Valid Request request,
                                    HttpServletRequest httpServletRequest) {
        var pollId = hashIdHandler.decode(request.pollHashId());
        var requirement = new VoteService.Requirement(
            pollId,
            request.pollItemIds(),
            IpAddressExtractor.extract(httpServletRequest),
            Optional.empty(),
            request.voterName()
        );

        service.vote(requirement);

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "투표 ID는 필수입니다.")
        String pollHashId,
        @NotEmpty(message = "투표 항목 ID는 필수입니다.")
        Set<Long> pollItemIds,
        Optional<String> voterName
    ) {
    }
}
