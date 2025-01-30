package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetTopPollsService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetTopPollsControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("전체 top10 투표 조회");

    @Autowired
    private GetTopPollsService service;

    @Test
    void success(){
        when(service.get(anyInt()))
            .thenReturn(mockResult());

        given()
            .queryParams("limit", 10)
            .when()
            .get("/v2/polls/top")
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
                    queryParameters(
                        parameterWithName("limit").description("")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                            fieldWithPath("data[].id").type(STRING).description("투표 해시 ID"),
                            fieldWithPath("data[].title").type(STRING).description("투표 제목"),
                            fieldWithPath("data[].creatorName").type(STRING).description("투표 생성자 이름"),
                            fieldWithPath("data[].creatorProfileUrl").type(STRING).description("투표 생성자 프로필 이미지"),
                            enumDescription(
                                fieldWithPath("data[].responseType").type(STRING).description("투표 응답 타입"),
                                Poll.PollResponseType.class
                            ),
                            enumDescription(
                                fieldWithPath("data[].pollType").type(STRING).description("투표 타입"),
                                Poll.PollType.class
                            ),
                            fieldWithPath("data[].endAt").type(STRING).description("투표 종료 시간"),
                            fieldWithPath("data[].readCount").type(NUMBER).description("조회수"),
                            fieldWithPath("data[].totalVotingCount").type(NUMBER).description("총 투표 수"),
                            fieldWithPath("data[].votedPeopleCount").type(NUMBER).description("투표한 사람 수"),
                            fieldWithPath("data[].items").type(ARRAY).description("투표 항목"),
                            fieldWithPath("data[].items[].id").type(NUMBER).description("투표 ID"),
                            fieldWithPath("data[].items[].description").type(STRING).description("투표 항목 설명"),
                            fieldWithPath("data[].items[].imageUrl").type(STRING).description("투표 항목 이미지 URL"),
                            fieldWithPath("data[].items[].votingCount").type(NUMBER).description("투표 항목 투표 수")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private List<PollSummary> mockResult() {
        return List.of(
            new PollSummary(
                1L,
                "title",
                "creatorName",
                "http://creatorProfileUrl",
                Poll.PollResponseType.SINGLE,
                Poll.PollType.NAMED,
                LocalDateTime.now(),
                0,
                0,
                0,
                List.of(
                    new PollSummary.PollItem(
                        1L,
                        "description",
                        "imageUrl",
                        0
                    ),
                    new PollSummary.PollItem(
                        2L,
                        "description",
                        "imageUrl",
                        0
                    )
                )
            )
        );
    }
}