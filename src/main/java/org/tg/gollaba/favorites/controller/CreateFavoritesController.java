package org.tg.gollaba.favorites.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.favorites.service.CreateFavoritesService;

@RestController
@RequestMapping("/v2/favorites")
@RequiredArgsConstructor
public class CreateFavoritesController {
    private final CreateFavoritesService service;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    ApiResponse<Void> create(AuthenticatedUser user,
                             @Valid @RequestBody Request request) {
        service.create(user.id(), request.pollId);
        return ApiResponse.success();
    }

    record Request(
        @NotNull(message = "pollId 는 필수값입니다.")
        Long pollId
    ) {
    }
}