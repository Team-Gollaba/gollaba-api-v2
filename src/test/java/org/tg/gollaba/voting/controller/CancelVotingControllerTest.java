package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CancelVotingControllerTest extends ControllerTestContext {

    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("투표 철회");

    @Test
    void success() {
        given()
            .when()
            .delete("/v2/voting/{votingId}", 1L)
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