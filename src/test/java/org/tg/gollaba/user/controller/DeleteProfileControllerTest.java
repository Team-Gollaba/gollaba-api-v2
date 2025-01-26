package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

public class DeleteProfileControllerTest extends ControllerTestContext {
    private static final String TAG = ControllerTestContext.Tags.USER.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.USER.descriptionWith("회원 프로필 이미지 제거");

    @Test
    @WithMockUser(authorities = "USER")
    void success(){
        given()
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .when()
            .delete("/v2/users/delete-profile")
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
