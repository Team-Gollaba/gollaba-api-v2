package org.tg.gollaba.poll.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.common.web.HashIdController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/v2/polls")
public class GetPollListController extends HashIdController {
    private final GetPollListService service;

    public GetPollListController(HashIdHandler hashIdHandler,
                                 GetPollListService service) {
        super(hashIdHandler);
        this.service = service;
    }

    @GetMapping
    ApiResponse<PageResponse<PollSummaryResponse>> get(@SortDefaults(
                                                                @SortDefault(sort = "createdAt", direction = DESC)
                                                             )
                                                             @PageableDefault Pageable pageable,
                                                             Request request) {
        request.validate();
        var requirement = createRequirement(request, pageable);
        var pageResult = service.get(requirement);
        var resultItems = pageResult.stream()
            .map(this::convertToResponse)
            .toList();

        return ApiResponse.success(
            new PageResponse<>(
                resultItems,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
            )
        );
    }

    private GetPollListService.Requirement createRequirement(Request request,
                                                             Pageable pageable) {
        return new GetPollListService.Requirement(
            request.optionGroup(),
            request.query(),
            request.isActive(),
            request.pollType(),
            pageable
        );
    }

    private PollSummaryResponse convertToResponse(PollSummary pollSummary) {
        return new PollSummaryResponse(
            createHashId(pollSummary.id()),
            pollSummary.title(),
            pollSummary.creatorName(),
            pollSummary.responseType(),
            pollSummary.pollType(),
            pollSummary.endAt(),
            pollSummary.readCount(),
            pollSummary.totalVotingCount(),
            pollSummary.items().stream()
                .map(item -> new PollSummaryResponse.PollItem(
                    item.id(),
                    item.description(),
                    item.imageUrl(),
                    item.votingCount()
                ))
                .toList()
        );
    }

    record Request(
        Optional<GetPollListService.OptionGroup> optionGroup,
        Optional<String> query,
        Optional<Poll.PollType> pollType,
        Optional<Boolean> isActive
    ) {
        public void validate() {
            var query = this.query
                .filter(StringUtils::hasText)
                .orElse(null);

            if (optionGroup.isPresent() && query == null
                || optionGroup.isEmpty() && query != null) {
                throw new BadRequestException(Status.INVALID_PARAMETER, "optionGroup과 query는 함께 입력해야 합니다.");
            }
        }
    }

    record PollSummaryResponse(
        String id,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer readCount,
        Integer totalVotingCount,
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
