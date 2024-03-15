package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.service.GetSelfService;

import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetSelfControllerTest extends ControllerTestContext {
    private static final String TAG = ControllerTestContext.Tags.USER.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.USER.descriptionWith("회원 수정");
    @Autowired
    private GetSelfService service;



    @Test
    @WithMockUser(authorities = "USER")
    void success(){
        when(service.get(any()))
            .thenReturn(new GetSelfService.UserDetails(
                "testName",
                "test@test.com",
                User.RoleType.USER,
                User.ProviderType.KAKAO,
                "http://test.com/profile",
                "http://test.com/background"
            ));

        given()
            .header(authHeader())
            .when()
            .get("/v2/users/me")
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
                    requestHeaderWithAuthorization(),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.name").type(STRING).description("사용자 이름"),
                            fieldWithPath("data.email").type(STRING).description("사용자 이메일"),
                            enumDescription(
                                fieldWithPath("data.roleType").description("사용자 권한 타입"),
                                User.RoleType.class
                            ),
                            enumDescription(
                                fieldWithPath("data.providerType").description("사용자 제공 타입"),
                                User.ProviderType.class
                            ),
                            fieldWithPath("data.profileImageUrl").type(STRING).description("프로필 이미지 URL"),
                            fieldWithPath("data.backgroundImageUrl").type(STRING).description("배경 이미지 URL")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}