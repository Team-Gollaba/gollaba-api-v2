package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.voting.service.CheckVotingService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CheckVotingControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("투표 여부 확인");
    @Autowired
    private CheckVotingService service;

    @Test
    void success() {
        when(service.check(any())).thenReturn(true);

        given()
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .body(requestBody())
            .when()
            .post("/v2/voting/check")
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
                        fieldWithPath("pollHashId").type(STRING).description("투표 ID")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(BOOLEAN).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private CheckVotingController.Request requestBody() {
        return new CheckVotingController.Request(
            testHashId()
        );
    }
}