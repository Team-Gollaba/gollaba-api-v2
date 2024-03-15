package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.tg.gollaba.common.ControllerTestContext;

import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class UpdateUserControllerTest extends ControllerTestContext{

    private static final String TAG = ControllerTestContext.Tags.USER.tagName();

    private static final String DESCRIPTION = ControllerTestContext.Tags.USER.descriptionWith("회원 수정");

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", authorities = "USER")
    void success() throws Exception{
        given()
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .formParam("name", "testName")
            .when()
            .put("/v2/users")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .summary(DESCRIPTION)
                        .tag(TAG)
                        .description("""
                         로그인 진행 시 액세스 토큰과 리프레시 토큰을 전달합니다.
                                                                         
                         액세스 토큰의 구조는 아래와 같습니다.
                                                                         
                         ```json
                        {
                          "sub": "2",
                          "role": "ROLE_USER",
                         "type": "access",
                          "exp": 1695633312
                        }
                         ```
                         
                         | 항목 | description                                               |
                         |------|-----------------------------------------------------------|
                         | sub  | 유저의 ID(PK)                                             |
                         | role | 권한(이메일 인증 시 ROLE_USER / 인증 안한 경우 ROLE_PENDING) |
                         | exp  | 유효기간                                                  |
                         | type | 토큰의 종류(access, refresh, email-verfication 등이 존재)   |
                          
                         """),
                    preprocessRequest(),
                    preprocessResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(NULL).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}