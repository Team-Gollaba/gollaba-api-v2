package org.tg.gollaba.voting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.service.UpdateVotingService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/voting/{votingId}")
@RequiredArgsConstructor
public class UpdateVotingController {
    private final UpdateVotingService service;

    @PutMapping (headers = HttpHeaders.AUTHORIZATION)
    public ApiResponse<Void> update(AuthenticatedUser user,
                                    @PathVariable Long votingId,
                                    @RequestBody @Valid Request request) {
        var requirement = createRequirement(
            user.id(),
            request,
            votingId
        );

        service.update(requirement);
        return ApiResponse.success();
    }

    private UpdateVotingService.Requirement createRequirement(Long userId,
                                                              Request request,
                                                              Long votingId) {
        return new UpdateVotingService.Requirement(
            userId,
            votingId,
            request.voterName(),
            request.pollItemIds()
        );
    }

    record Request(
        Optional<String> voterName,
        Optional<Set<Long>> pollItemIds
    ) {
    }
}
