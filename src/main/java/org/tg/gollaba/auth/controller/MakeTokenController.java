package org.tg.gollaba.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.common.web.AdminUtilController;
import org.tg.gollaba.common.web.ApiResponse;

@RestController
@RequestMapping("/v2/auth/make-token")
public class MakeTokenController extends AdminUtilController {
    private final JwtTokenHandler jwtTokenHandler;

    public MakeTokenController(@Value("${security.admin-key}") String adminKey,
                               JwtTokenHandler jwtTokenHandler) {
        super(adminKey);
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @PostMapping
    ApiResponse<String> make(@RequestBody Request request) {
        var accessToken = jwtTokenHandler.createAccessToken(request.userId());

        return ApiResponse.success(accessToken);
    }

    record Request(
        Long userId,
        String adminKey
    ) {
    }

}
