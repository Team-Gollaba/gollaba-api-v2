package org.tg.gollaba.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.common.web.ApiResponse;

@RestController
@RequestMapping("/v2/auth/make-token")
@RequiredArgsConstructor
public class TestTokenController {
    private final JwtTokenHandler jwtTokenHandler;

    @PostMapping
    ApiResponse<String> make(@RequestBody Request request) {
        var accessToken = jwtTokenHandler.createAccessToken(request.userId());

        return ApiResponse.success(accessToken);
    } //테스트시 여기서 유저 아이디 입력하면 토큰 발급함 << 이거쓰기
    //ㄴ> Auth 어쩌구 헤더 값에 Barer 토큰넣기

    record Request(
        Long userId
    ) {
    }

}
