package org.tg.gollaba.favorites.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.favorites.service.DeleteFavoritesService;
import org.tg.gollaba.common.web.HashIdHandler;


@RestController
@RequestMapping("/v2/favorites")
@RequiredArgsConstructor
public class DeleteFavoritesController {
    private final DeleteFavoritesService service;
    private final HashIdHandler hashIdHandler;

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping
    ApiResponse<Void> delete(AuthenticatedUser user,
                             @Valid @RequestBody Request request) {
        var pollId = hashIdHandler.decode(request.pollHashId);

        service.delete(user.id(), pollId);

        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "pollHashId는 필수입니다.")
        String pollHashId
    ) {
    }
}
