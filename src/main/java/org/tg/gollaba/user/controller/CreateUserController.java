package org.tg.gollaba.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.service.CreateUserService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class CreateUserController {
    private final CreateUserService service;

    @PostMapping
    ApiResponse<Response> create(@Valid Request request) {
        var userId = service.create(request.toRequirement());

        return ApiResponse.success(
            new Response(userId)
        );
    }

    record Request(
        String email,
        String name,
        Optional<String> password,
        Optional<String> profileImageUrl,
        Optional<User.ProviderType> providerType,
        Optional<String> providerId
    ) {
        public CreateUserService.Requirement toRequirement() {
            return new CreateUserService.Requirement(
                email,
                name,
                password,
                profileImageUrl,
                providerType,
                providerId
            );
        }
    }

    record Response(
        Long id
    ) {
    }
}
