package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.voting.service.GetMyVotingService;
import org.tg.gollaba.voting.vo.VotingVo;

import java.time.LocalDateTime;
import java.util.Set;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetMyVotingControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("내 투표 참여 조회");
    @Autowired
    private GetMyVotingService service;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        when(service.getMyVoting(any(), any()))
            .thenReturn(mockResult());

        given()
            .header(authHeader())
            .queryParam("pollHashId", testHashId())
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
                    queryParameters(
                        parameterWithName("pollHashId").description("투표 ID")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(NUMBER).description("투표 참여 ID")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private VotingVo mockResult() {
        return new VotingVo(
            1L,
            1L,
            1L,
            "voterName",
            LocalDateTime.now(),
            Set.of(
                new VotingVo.Item(
                    1L,
                    1L
                )
            )
        );
    }

}