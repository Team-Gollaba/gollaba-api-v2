package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.service.GetMyFavoritePollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/v2/polls/favorites-me")
public class GetMyFavoritePollListController extends HashIdController {
    private final GetMyFavoritePollListService service;

    public GetMyFavoritePollListController(HashIdHandler hashIdHandler, GetMyFavoritePollListService service) {
        super(hashIdHandler);
        this.service = service;
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    ApiResponse<PageResponse<PollSummaryResponse>> get(AuthenticatedUser user,
                                               @SortDefault.SortDefaults(
                                                   @SortDefault(sort = "createdAt", direction = DESC)
                                               )
                                               @PageableDefault Pageable pageable){
        var pollSummaries = service.get(user.id(), pageable);

        return ApiResponse.success(
            convertToResponse(PageResponse.from(pollSummaries))
        );
    }
}
