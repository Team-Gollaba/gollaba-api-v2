package org.tg.gollaba.favorites.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class DeleteFavoritesControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.FAVORITES.tagName();
    private static final String DESCRIPTION = Tags.FAVORITES.descriptionWith("삭제");

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        given()
            .header(authHeader())
            .body(Map.of("pollHashId", testHashId()))
            .when()
            .delete("/v2/favorites", 1L)
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
                    requestFields(
                        fieldWithPath("pollHashId").type("String").description("해시 아이디")
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


