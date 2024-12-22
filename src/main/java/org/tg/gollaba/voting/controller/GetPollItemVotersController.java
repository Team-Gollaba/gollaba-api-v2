package org.tg.gollaba.voting.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.voting.service.GetPollItemVotersService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/voting/voter")
@RequiredArgsConstructor
public class GetPollItemVotersController {
    private final GetPollItemVotersService service;
    private final HashIdHandler hashIdHandler;

    @GetMapping
    ApiResponse<List<Response>> get(@RequestParam
                              @NotBlank(message = "pollHashId 는 필수 값입니다.")
                              String pollHashId){
        var pollId = hashIdHandler.decode(pollHashId);
        var votingItemVoterNames = service.getVotedVoterNames(pollId);

        return ApiResponse.success(
            votingItemVoterNames.stream()
                .map(item -> new Response(
                    (Long) item.get("pollItemId"),
                    (List<String>) item.get("voterNames"))
                )
                .toList()
        );
    }

    record Response(
        Long pollItemId,
        List<String> voterNames
    ) {
    }
}
