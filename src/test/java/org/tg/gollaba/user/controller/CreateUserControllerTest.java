package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CreateUserControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.USER.tagName();
    private static final String DESCRIPTION = Tags.USER.descriptionWith("회원 가입");

    @Test
    void success() {
        given()
            .body(requestBody())
            .when()
            .post("/v2/users/signup")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    preprocessRequest(),
                    preprocessResponse(),
                    requestFields(
                        fieldWithPath("email").type(STRING).description("이메일"),
                        fieldWithPath("name").type(STRING).description("이름"),
                        fieldWithPath("password").type(STRING).optional().description("비밀번호"),
                        fieldWithPath("profileImageUrl").type(STRING).optional().description("프로필 이미지 URL"),
                        fieldWithPath("providerType").type(STRING).optional().description("소셜 로그인 타입"),
                        fieldWithPath("providerId").type(STRING).optional().description("소셜 로그인 ID"),
                        fieldWithPath("providerAccessToken").type(STRING).optional().description("앱 사용자 액세스 토큰")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(NUMBER).description("생성된 사용자 ID")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private CreateUserController.Request requestBody() {
        return new CreateUserController.Request(
            "email",
            "name",
            Optional.of("password"),
            Optional.of("profileImageUrl"),
            Optional.of(User.ProviderType.KAKAO),
            Optional.of("providerId"),
            Optional.of("providerAccessToken")
        );
    }
}