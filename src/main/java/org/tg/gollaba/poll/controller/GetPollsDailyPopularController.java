package org.tg.gollaba.poll.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdController;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.service.GetPollsDailyPopularService;
import org.tg.gollaba.poll.vo.PagePollSummary;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/polls/daily/popular")
public class GetPollsDailyPopularController extends HashIdController {
    private final GetPollsDailyPopularService service;

    public GetPollsDailyPopularController(HashIdHandler hashIdHandler,
                                   GetPollsDailyPopularService service){
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResponse<PagePollSummary>> get(Pageable pageable){

        var pollSummaries = service.get(pageable);

        var pagePollSummaries = convertPollId(pollSummaries);

        return ApiResponse.success(
            new PageResponse<>(
                pagePollSummaries.getContent(),
                pagePollSummaries.getNumber(),
                pagePollSummaries.getSize(),
                pagePollSummaries.getTotalElements(),
                pagePollSummaries.getTotalPages()
            ));
    }

    private Page<PagePollSummary> convertPollId(Page<PollSummary> pollSummaries) {
        List<PagePollSummary> hashedPollSummary = pollSummaries.getContent()
            .stream()
            .map(pollSummary -> new PagePollSummary(
                createHashId(pollSummary.id()),
                pollSummary.title(),
                pollSummary.creatorName(),
                pollSummary.responseType(),
                pollSummary.pollType(),
                pollSummary.endAt(),
                pollSummary.readCount(),
                pollSummary.totalVotingCount(),
                PagePollSummary.PollItem.to(pollSummary.items())
            ))
            .collect(Collectors.toList());

        return new PageImpl<>(
            hashedPollSummary,
            pollSummaries.getPageable(),
            pollSummaries.getTotalElements()
        );
    }
}
