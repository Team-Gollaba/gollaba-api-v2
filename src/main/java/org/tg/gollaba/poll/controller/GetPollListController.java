package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
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
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/v2/polls")
@RequiredArgsConstructor
public class GetPollListController {
    private final GetPollListService service;

    @GetMapping
    ApiResponse<PageResponse<GetPollListService.PollSummary>> get(@SortDefaults(
                                                                    @SortDefault(sort = "createdAt", direction = DESC)
                                                                 )
                                                                  @PageableDefault Pageable pageable,
                                                                  Request request) {
        request.validate();
        var requirement = createRequirement(request, pageable);
        var result = service.get(requirement);

        return ApiResponse.success(
            PageResponse.from(result)
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
}
