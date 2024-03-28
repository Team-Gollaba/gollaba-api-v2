package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;

import java.util.Optional;
import java.util.Set;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class VoteControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("참여");

    @Test
    void success() {
        given()
            .body(requestBody())
            .when()
            .post("/v2/voting")
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
                    requestFields(
                        fieldWithPath("pollHashId").type(STRING).description("투표 ID"),
                        fieldWithPath("pollItemIds").type(ARRAY).description("투표 항목 ID"),
                        fieldWithPath("voterName").type(STRING).description("투표자 이름")
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

    private VoteController.Request requestBody() {
        return new VoteController.Request(
            testHashId(),
            Set.of(1L, 2L),
            Optional.of("testVoterName")
        );
    }
}