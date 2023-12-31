package org.tg.gollaba.voting.infrastructure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.application.UpdateVotingService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/voting/{votingId}")
@RequiredArgsConstructor
public class UpdateVotingController {
    private final UpdateVotingService service;

    @PutMapping
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
