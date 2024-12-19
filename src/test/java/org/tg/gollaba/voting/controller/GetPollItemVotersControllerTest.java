package org.tg.gollaba.voting.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.voting.controller.GetPollItemVotersController.Response;
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
            .thenReturn(mockResult().pollItemVotedNamesMap());

        given()
            .when()
            .get("/v2/voted-users/{pollHashId}", testHashId())
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
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.pollItemVotedNamesMap").type(ARRAY).description("투표 항목과 사용자 이름 리스트의 매핑 배열"),
                            fieldWithPath("data.pollItemVotedNamesMap[].pollItemId").type(NUMBER).description("투표 항목 ID"),
                            fieldWithPath("data.pollItemVotedNamesMap[].voterNames").type(ARRAY).description("해당 투표 항목에 투표한 사용자 이름 리스트")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

/*
* {dynamicKey}는 실제 동적 키의 자리를 나타냅니다. 예를 들어, "1"이나 "2"와 같은 키가 될 수 있습니다. 실제 코드에서 해당 키가 무엇인지를 동적으로 파악해서 문서화할 때는 이 자리에 실제 키 값을 넣어줘야 합니다.
 */
//    private List<Map<Long, List<String>>> mockResult() {
//        var pollItemVotedNames = Map.of(
//            1L, List.of("voter1", "voter3"),
//            2L, List.of("voter2")
//        );
//
//        return List.of(pollItemVotedNames);
//    }

    private Response mockResult() {
        return new Response(
            List.of(
                Map.of(
                    "pollItemId", 1L,
                    "voterNames", List.of("voter1", "voter2")
                ),
                Map.of(
                    "pollItemId", 2L,
                    "voterNames", List.of("voter3")
                )
            )
        );
    }
}
