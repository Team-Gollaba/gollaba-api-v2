package org.tg.gollaba.favorites.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.favorites.service.GetMyFavoritesService;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;
import static org.tg.gollaba.favorites.service.GetMyFavoritesService.*;

class GetMyFavoritesControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.FAVORITES.tagName();
    private static final String DESCRIPTION = Tags.FAVORITES.descriptionWith("좋아요 목록 조회");

    @Autowired
    private GetMyFavoritesService service;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        when(service.get(any())).thenReturn(List.of(
            new FavoritesSummary(1L, 1L, 1L),
            new FavoritesSummary(2L, 2L, 2L),
            new FavoritesSummary(3L, 3L, 3L)
            )
        );

        given()
            .header(authHeader())
            .when()
            .get("/v2/favorites/me")
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
                            fieldWithPath("data").type(ARRAY).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}