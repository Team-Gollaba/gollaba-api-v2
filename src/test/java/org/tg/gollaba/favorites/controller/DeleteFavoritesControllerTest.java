package org.tg.gollaba.favorites.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class DeleteFavoritesControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.USER.tagName();
    private static final String DESCRIPTION = Tags.USER.descriptionWith("좋아요 요청 철회");

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void success() {
        given()
            .header("Authorization", "Bearer " + "accessToken") //TODO 토큰 주입 필요
            .when()
            .delete("/v2/favorites/{favoritesId}", 1L)
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
                        fieldWithPath("pollId").type(STRING).description("투표 ID"),
                        fieldWithPath("favoritesId").type(STRING).description("좋아요 ID")
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


