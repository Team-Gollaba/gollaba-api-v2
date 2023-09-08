package org.tg.gollaba.vote.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.vote.application.CreateVoteService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/polls/{pollId}/vote")
@RequiredArgsConstructor
public class VoteController {
    private final CreateVoteService service;

    @PostMapping
    public ApiResponse<Void> vote(@PathVariable Long pollId,
                                  @RequestBody @Valid Request request,
                                  HttpServletRequest httpServletRequest) {
        var requirement = createRequirement(
            pollId,
            request,
            httpServletRequest
        );

        service.create(requirement);

        return ApiResponse.success();
    }

    private CreateVoteService.Requirement createRequirement(Long pollId,
                                                            Request request,
                                                            HttpServletRequest httpServletRequest) {
        return new CreateVoteService.Requirement(
            pollId,
            request.pollItemIds(),
            "",
            // TODO: IP 주소 추가
            request.userId(),
            request.voterName()
        );
    }

    record Request(
        Set<Long> pollItemIds,
        Optional<Long> userId,
        Optional<String> voterName
    ) {
    }
}
