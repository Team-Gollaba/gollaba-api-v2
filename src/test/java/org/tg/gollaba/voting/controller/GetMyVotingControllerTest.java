package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.voting.service.GetMyVotingService;
import org.tg.gollaba.voting.service.GetMyVotingService.VotingDetailVo;

import java.util.Arrays;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetMyVotingControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("내 투표 참여 조회");

    @Autowired
    private GetMyVotingService service;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        when(service.getMyVoting(any(), any(), any()))
            .thenReturn(mockResult());

        given()
            .header(authHeader())
            .param("pollHashId", testHashId())
            .when()
            .get("/v2/voting/me")
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
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.votingId").type(NUMBER).description("투표 참여 ID"),
                            fieldWithPath("data.votedItemIds").type(ARRAY).description("투표한 항목의 Ids")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private VotingDetailVo mockResult() {
        return new VotingDetailVo(
            1L,
            Arrays.asList(1L, 2L)
        );
    }
}