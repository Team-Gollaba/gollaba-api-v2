package org.tg.gollaba.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.auth.service.LoginByProviderTokenService;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.user.domain.User;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class LoginByProviderTokenControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.AUTHORIZATION.tagName();
    private static final String DESCRIPTION = Tags.AUTHORIZATION.descriptionWith("로그인 by Provider Token");

    @Autowired
    private LoginByProviderTokenService loginByProviderTokenService;

    @Test
    void success() {
        var request = new LoginByProviderTokenController.Request(
            "accessToken",
            User.ProviderType.KAKAO
        );

        when(loginByProviderTokenService.login(request.providerToken(), request.providerType()))
            .thenReturn(new IssuedToken("accessToken", "refreshToken"));

        given()
            .body(request)
            .when()
            .post("/v2/auth/login/by-provider-token")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    requestFields(
                        enumDescription(
                            fieldWithPath("providerType").type(STRING).description("OAuth2 Provider 타입"),
                            User.ProviderType.class
                        ),
                        fieldWithPath("providerToken").type(STRING).description("Provider에서 발급받은 엑세스 토큰")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.accessToken").type(STRING).description("발급된 액세스 토큰")
                        )
                    )
                )
            )
            .statusCode(HttpStatus.OK.value());
    }
}