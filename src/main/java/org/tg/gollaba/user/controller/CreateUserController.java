package org.tg.gollaba.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.service.CreateUserService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/users/signup")
@RequiredArgsConstructor
public class CreateUserController {
    private final CreateUserService service;

    @PostMapping
    ApiResponse<Response> create(@Valid @RequestBody Request request) {
        request.validate();
        var accessToken = service.create(request.toRequirement());

        return ApiResponse.success(
            new Response(accessToken)
        );
    }

    record Request(
        @NotBlank(message = "email 은 필수 입니다.")
        String email,
        @NotBlank(message = "name 은 필수 입니다.")
        String name,
        Optional<String> password,
        Optional<String> profileImageUrl,
        Optional<User.ProviderType> providerType,
        Optional<String> providerId,
        Optional<String> providerAccessToken
    ) {
        public void validate() {
            if (providerType.isPresent() && providerId.isEmpty()
                || providerType.isEmpty() && providerId.isPresent()) {
                throw new BadRequestException(Status.INVALID_PARAMETER, "providerType, providerId 둘 다 필요합니다.");
            }

            if (providerType.isEmpty() && providerId().isEmpty()) {
                if (password.isEmpty()) {
                    throw new BadRequestException(Status.INVALID_PARAMETER, "password 는 필수입니다.");
                }
            }
        }

        public CreateUserService.Requirement toRequirement() {
            return new CreateUserService.Requirement(
                email,
                name,
                password,
                profileImageUrl,
                providerType,
                providerId,
                providerAccessToken
            );
        }
    }

    record Response(
        String accessToken
    ) {
    }
}
