package org.tg.gollaba.voting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.service.UpdateVotingService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/voting/{votingId}")
@RequiredArgsConstructor
public class UpdateVotingController {
    private final UpdateVotingService service;

    @PutMapping //TODO user 받기
    public ApiResponse<Void> update(@PathVariable Long votingId,
                                    @RequestBody @Valid Request request) {
        var requirement = createRequirement(
            request,
            votingId
        );

        service.update(requirement);
        return ApiResponse.success();
    }

    private UpdateVotingService.Requirement createRequirement(Request request,
                                                              Long votingId) {
        return new UpdateVotingService.Requirement(
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
