package org.tg.gollaba.vote.presentaion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.vote.application.VoteService;

import java.util.Optional;

@RestController
@RequestMapping("/v1/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService service;

    @PostMapping
    public ApiResponse<Long> create() {

        return null;
    }

    record Request(
        Long pollOptionId,
        Optional<Long> userId,
        Optional<String> voterName
    ) {
        public VoteService.Requirement toRequirement() {
            return new VoteService.Requirement(
                pollOptionId,
                userId,
                voterName
            );
        }
    }
}
