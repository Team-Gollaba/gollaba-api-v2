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
import org.tg.gollaba.common.web.HashIdHandler;

@RestController
@RequestMapping("/v2/favorites")
@RequiredArgsConstructor
public class CreateFavoritesController {
    private final CreateFavoritesService service;
    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    ApiResponse<Response> create(AuthenticatedUser user,
                             @Valid @RequestBody Request request) {
        var pollId = hashIdHandler.decode(request.pollHashId());

        return ApiResponse.success(
            new Response(service.create(user.id(), pollId))
        );
    }

    record Request(
        @NotNull(message = "pollId 는 필수값입니다.")
        String pollHashId
    ) {
    }

    record Response(
        Long id
    ) {
    }
}