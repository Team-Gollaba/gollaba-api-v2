package org.tg.gollaba.poll.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollDetailsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v2/polls/{pollHashId}")
public class GetPollDetailsController extends HashIdController {
    private final GetPollDetailsService service;

    public GetPollDetailsController(HashIdHandler hashIdHandler,
                                    GetPollDetailsService service) {
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    ApiResponse<PollDetailsResponse> get(@PathVariable String pollHashId) {
        var pollId = getPollId(pollHashId);

        var pollDetails = service.get(pollId);

        return ApiResponse.success(
            convertToResponse(pollDetails)
        );
    }

    private PollDetailsResponse convertToResponse(GetPollDetailsService.PollDetails pollDetails) {
        return new PollDetailsResponse(
            createHashId(pollDetails.id()),
            pollDetails.title(),
            pollDetails.creatorName(),
            pollDetails.creatorProfileUrl(),
            pollDetails.responseType(),
            pollDetails.pollType(),
            pollDetails.endAt(),
            pollDetails.totalVotingCount(),
            pollDetails.votedPeopleCount(),
            pollDetails.readCount(),
            pollDetails.items().stream()
                .map(item -> new PollDetailsResponse.PollItem(
                    item.id(),
                    item.description(),
                    item.imageUrl(),
                    item.votingCount()
                ))
                .toList()
        );
    }

    record PollDetailsResponse(
        String id,
        String title,
        String creatorName,
        String creatorProfileUrl,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer totalVotingCount,
        Integer votedPeopleCount,
        Integer readCount,
        List<PollItem> items
    ) {
        record PollItem(
            Long id,
            String description,
            String imageUrl,
            Integer votingCount
        ) {
        }
    }
}
