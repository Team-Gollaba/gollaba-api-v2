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
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetMyPollsService;
import org.tg.gollaba.poll.vo.PollSummary;

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
    public ApiResponse<PageResponse<PollSummary>> get(AuthenticatedUser user,
                                                      @SortDefault.SortDefaults(
                                                          @SortDefault(sort = "createdAt", direction = DESC)
                                                      )
                                                      @PageableDefault Pageable pageable) {
        var pollSummaries = service.get(user.id(), pageable);

        return ApiResponse.success(
            PageResponse.from(pollSummaries)
        );
    }
}