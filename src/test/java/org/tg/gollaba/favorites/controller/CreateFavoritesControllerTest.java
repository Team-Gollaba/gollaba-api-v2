package org.tg.gollaba.favorites.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.favorites.service.CreateFavoritesService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CreateFavoritesControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.FAVORITES.tagName();
    private static final String DESCRIPTION = Tags.FAVORITES.descriptionWith("생성");
    @Autowired
    private CreateFavoritesService service;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        when(service.create(any(), any()))
            .thenReturn(1L);

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
                    requestFields(
                        fieldWithPath("pollHashId").type("String").description("해시 아이디")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(NUMBER).description("좋아요 아이디")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private CreateFavoritesController.Request requestBody() {
        return new CreateFavoritesController.Request(testHashId());
    }
}


