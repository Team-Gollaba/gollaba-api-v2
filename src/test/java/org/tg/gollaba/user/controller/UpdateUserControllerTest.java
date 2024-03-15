package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

class UpdateUserControllerTest extends ControllerTestContext{

    private static final String TAG = ControllerTestContext.Tags.USER.tagName();

    private static final String DESCRIPTION = ControllerTestContext.Tags.USER.descriptionWith("회원 수정");

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void success() throws Exception{
        var tokenResponse = mockMvc.perform(post("/v2/auth/make-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": 1}"))
            .andExpect(status().isOk())
            .andReturn().getResponse()
            .getContentAsString();

        var accessToken = objectMapper.readTree(tokenResponse)
                                             .get("data")
                                             .textValue();

        given()
            .header("Authorization", "Bearer " + accessToken)
            .body(requestBody())
            .when()
            .post("/v2/users/update")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer 토큰")
                    ),
                    requestFields(
                        fieldWithPath("name").type(STRING).description("이름"),
                        fieldWithPath("profileImage").type(OBJECT).optional().description("프로필 이미지 파일"),
                        fieldWithPath("backgroundImage").type(OBJECT).optional().description("백그라운드 이미지 파일")
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

    private UpdateUserController.Request requestBody() {
        return new UpdateUserController.Request(
            "name",
            Optional.ofNullable(null),
            Optional.ofNullable(null)
        );
    }
}