package org.tg.gollaba.participation.infrastructure;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import static io.restassured.RestAssured.patch;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;

class CancelParticipationControllerTest extends ControllerTestContext {

    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("투표 철회");

    @Test
    void success() {
        given()
            //.pathParam("participantId", 1L) // * 저 이거 너무 갑갑해서 올리고 그냥 한 소리 듣고싶엇어요 ....
            .when()
            .delete("/v2/participation/{participantId}")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    requestFields(
                        fieldWithPath("participantId").type(NUMBER).description("투표 참여 ID")
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