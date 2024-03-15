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
    @WithMockUser(authorities = "USER")
    void success() throws Exception{
        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
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
                        .description(
                            """
                            | 항목 | description |
                            |------|-------------|
                            | name | type: String, 설명: 이름        |
                            | profileImage | type: MultipartFile, 설명: 프로필 이미지 |
                            | backgroundImage | type: MultipartFile, 설명: 배경 이미지 |  
                            """
                        ),
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