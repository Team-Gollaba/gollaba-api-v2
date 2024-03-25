package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.component.HashIdHandler;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetMyPollsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/v2/polls/me")
@RequiredArgsConstructor
public class GetMyPollsController {
    private final GetMyPollsService service;

    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ApiResponse<PageResponse<PagePollSummary>> get(AuthenticatedUser user,
                                                      @SortDefault.SortDefaults(
                                                          @SortDefault(sort = "createdAt", direction = DESC)
                                                      )
                                                      @PageableDefault Pageable pageable) {
        var pollSummaries = service.get(user.id(), pageable);

        var pollSummaryPage = convertPollId(pollSummaries, hashIdHandler);

        return ApiResponse.success(
            new PageResponse<>(
                pollSummaryPage.getContent(),
                pollSummaryPage.getNumber(),
                pollSummaryPage.getSize(),
                pollSummaryPage.getTotalElements(),
                pollSummaryPage.getTotalPages()
            )
        );
    }

    private Page<PagePollSummary> convertPollId(Page<GetMyPollsService.PollSummary> pollSummaries,
                                                HashIdHandler hashIdHandler) {
        List<PagePollSummary> hashedPollSummary = pollSummaries.getContent()
            .stream()
            .map(pollSummary -> new PagePollSummary(
                hashIdHandler.encode(pollSummary.id()),
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

    public record PagePollSummary(
        String id,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer readCount,
        Integer totalVotingCount,
        List<PagePollSummary.PollItem> items
    ) {
        public record PollItem(
            Long id,
            String description,
            String imageUrl,
            Integer voteCount
        ) {
            private static List<PollItem> to(List<GetMyPollsService.PollSummary.PollItem> pollItems) {

                return pollItems.stream()
                    .map(pollItem -> new PollItem(
                        pollItem.id(),
                        pollItem.description(),
                        pollItem.imageUrl(),
                        pollItem.voteCount()
                    ))
                    .collect(Collectors.toList());
            }
        }
    }
}