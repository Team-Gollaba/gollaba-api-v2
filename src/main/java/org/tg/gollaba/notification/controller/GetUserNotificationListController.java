package org.tg.gollaba.notification.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.notification.service.GetUserNotificationListService;
import org.tg.gollaba.notification.vo.AppNotificationVo;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/v2/app-notifications")
@RequiredArgsConstructor
public class GetUserNotificationListController {
    private final GetUserNotificationListService service;

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping
    ApiResponse<PageResponse<AppNotificationVo>> get(AuthenticatedUser user,
                                                    @SortDefault.SortDefaults(
                                                      @SortDefault(sort = "createdAt", direction = DESC)
                                                    )
                                                    @PageableDefault Pageable pageable){
        var notifications = service.get(user.id(), pageable);
        return ApiResponse.success(PageResponse.from(notifications));
    }
}
