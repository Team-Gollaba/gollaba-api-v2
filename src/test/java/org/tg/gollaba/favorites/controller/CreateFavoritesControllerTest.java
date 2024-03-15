package org.tg.gollaba.favorites.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CreateFavoritesControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.FAVORITES.tagName();
    private static final String DESCRIPTION = Tags.FAVORITES.descriptionWith("좋아요 생성");

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        given()
            .header(authHeader())
            .body(requestBody())
            .when()
            .post("/v2/favorites")
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
                            fieldWithPath("data").type(NULL).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private CreateFavoritesController.Request requestBody() {
        return new CreateFavoritesController.Request(1L);
    }
}


