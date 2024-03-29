package org.tg.gollaba.favorites.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.favorites.service.GetMyFavoritesService;
import org.tg.gollaba.poll.component.HashIdHandler;

import java.util.List;

import static org.tg.gollaba.favorites.service.GetMyFavoritesService.*;

@RestController
@RequestMapping("/v2/favorites/me")
@RequiredArgsConstructor
public class GetMyFavoritesController {
    private final GetMyFavoritesService service;
    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    ApiResponse<List<String>> get(AuthenticatedUser user) {
        var favoritesList = service.get(user.id());
        var response =favoritesList.stream()
            .map(FavoritesSummary::id)
            .map(hashIdHandler::encode)
            .toList();

        return ApiResponse.success(response);
    }
}