package org.tg.gollaba.voting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.support.IpAddressExtractor;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.service.CancelVotingService;

@RestController
@RequestMapping("/v2/voting/{votingId}")
@RequiredArgsConstructor
public class CancelVotingController {
    private final CancelVotingService service;

    @PreAuthorize("hasAnyAuthority('USER')")
    @DeleteMapping
    public ApiResponse<Void> cancel(@PathVariable
                                    @NotNull(message = "비회원일 경우, 투표를 취소할 수 없습니다.")
                                    Long votingId,
                                    HttpServletRequest httpServletRequest){
        var ipAddress = IpAddressExtractor.extract(httpServletRequest);
        service.cancel(votingId, ipAddress);

        return ApiResponse.success();
    }
}
