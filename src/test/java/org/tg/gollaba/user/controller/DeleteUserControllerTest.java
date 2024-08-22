package org.tg.gollaba.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class DeleteUserControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.USER.tagName();
    private static final String DESCRIPTION = Tags.USER.descriptionWith("회원 탈퇴");

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        given()
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .when()
            .delete("/v2/users/sign-out")
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