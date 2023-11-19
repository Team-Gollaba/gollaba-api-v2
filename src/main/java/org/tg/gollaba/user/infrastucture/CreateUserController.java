package org.tg.gollaba.user.infrastucture;

import org.tg.gollaba.common.web.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.user.application.CreateUserService;

import java.util.Optional;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
class CreateUserController {
    private final CreateUserService service;

    @PostMapping
    ApiResponse<Response> signup(@Valid SignRequest request){

        var requirement = new CreateUserService.Requirement(
            request.email,
            request.nickName,
            request.password,
            Optional.ofNullable(
                request.profileImage
            ),
            Optional.ofNullable(
                request.backgroundImage
            )
        );

        var userId = service.signup(requirement);

        return ApiResponse.success(
            new Response(userId)
        );
    }

    record SignRequest(

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                 message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank
        @Size(min = 2, max = 20)
        String nickName,

        @NotBlank
        String password,

        MultipartFile profileImage,

        MultipartFile backgroundImage

        //TODO auth 추가 (providerType, providerId)
    ){}

    record Response(
        Long id
    ){}
}
