package org.tg.gollaba.voting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.support.IpAddressExtractor;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.voting.service.CheckVotingService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/voting/check")
@RequiredArgsConstructor
public class CheckVotingController {
    private final HashIdHandler hashIdHandler;
    private final CheckVotingService service;

    @PostMapping
    ApiResponse<Boolean> check(@Valid @RequestBody Request request,
                               HttpServletRequest httpServletRequest) {
        var pollId = hashIdHandler.decode(request.pollHashId());
        var requirement = new CheckVotingService.Requirement(
            pollId,
            IpAddressExtractor.extract(httpServletRequest),
            Optional.empty()
        );

        return ApiResponse.success(
            service.check(requirement)
        );
    }

    @PostMapping(headers = HttpHeaders.AUTHORIZATION)
    ApiResponse<Boolean> check(@Valid @RequestBody Request request,
                               AuthenticatedUser user,
                               HttpServletRequest httpServletRequest) {
        var pollId = hashIdHandler.decode(request.pollHashId());
        var requirement = new CheckVotingService.Requirement(
            pollId,
            IpAddressExtractor.extract(httpServletRequest),
            Optional.of(user.id())
        );

        return ApiResponse.success(
            service.check(requirement)
        );
    }

    record Request(
        @NotBlank(message = "pollHashId 는 필수값입니다.")
        String pollHashId
    ) {
    }
}
