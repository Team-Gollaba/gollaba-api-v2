package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.voting.service.GetPollItemVotersService;

import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;


public class GetPollItemVotersControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.VOTING.tagName();
    private static final String DESCRIPTION = Tags.VOTING.descriptionWith("투표 참여자 이름 조회");

    @Autowired
    private GetPollItemVotersService service;

    @Test
    void success(){
        when(service.getVotedVoterNames(anyLong()))
            .thenReturn(mockResult());

        given()
            .when()
            .get("/v2/voting/voter?pollHashId={pollHashId}", testHashId())
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
                            fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                            fieldWithPath("data[].pollItemId").type(NUMBER).description("투표 항목 ID"),
                            fieldWithPath("data[].voterNames").type(ARRAY).description("해당 투표 항목에 투표한 사용자 이름 리스트")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private List<Map<String, Object>> mockResult() {
        return (
            List.of(
                Map.of(
                    "pollItemId", 1L,
                    "voterNames", List.of("voter1#59182", "voter2#19283")
                ),
                Map.of(
                    "pollItemId", 2L,
                    "voterNames", List.of("voter3#48123")
                )
            )
        );
    }
}
