package org.tg.gollaba.poll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.service.GetMyFavoritePollListService;
import org.tg.gollaba.poll.vo.PollSummary;

@RestController
@RequestMapping("/v2/polls/favorites-me")
@RequiredArgsConstructor
public class GetMyFavoritePollListController {
    private final GetMyFavoritePollListService service;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    ApiResponse<PageResponse<PollSummary>> get(AuthenticatedUser user,
                                               @PageableDefault Pageable pageable){
        var pollSummaries = service.get(user.id(), pageable);

        return ApiResponse.success(
            PageResponse.from(pollSummaries)
        );
    }
}
