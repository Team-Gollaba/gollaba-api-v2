package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;


import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class ChangeProfileControllerTest extends ControllerTestContext {
    private static final String TAG = ControllerTestContext.Tags.USER.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.USER.descriptionWith("유저 프로필 변경");

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "USER")
    void success() throws Exception{
        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .multiPart("image", imageFile())
            .when()
            .post("/v2/users/change-profile")
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
                            | image | type: MultipartFile, 설명: 프로필 이미지 |  
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