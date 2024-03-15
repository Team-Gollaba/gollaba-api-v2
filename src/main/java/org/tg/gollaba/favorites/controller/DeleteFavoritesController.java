package org.tg.gollaba.favorites.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.favorites.service.DeleteFavoritesService;


@RestController
@RequestMapping("/v2/favorites/{favoritesId}")
@RequiredArgsConstructor
public class DeleteFavoritesController {
    private final DeleteFavoritesService service;

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping
    ApiResponse<Void> delete(AuthenticatedUser user,
                             @Valid @PathVariable @NotNull(message = "favoriteId 는 필수값입니다.")
                                                    Long favoritesId) {
        service.delete(user.id(), favoritesId);

        return ApiResponse.success();
    }
}
