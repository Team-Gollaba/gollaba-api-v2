package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollDetailsService;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetPollDetailsControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("상세 조회");
    @Autowired
    private GetPollDetailsService getPollDetailsService;

    @Test
    void success() {
        when(getPollDetailsService.get(anyLong()))
            .thenReturn(mockResult());

        given()
            .when()
            .get("/v2/polls/{pollHashId}", testHashId())
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
                            fieldWithPath("data.id").type(STRING).description("투표 hash ID"),
                            fieldWithPath("data.title").type(STRING).description("투표 제목"),
                            fieldWithPath("data.creatorName").type(STRING).description("투표 생성자 이름"),
                            fieldWithPath("data.creatorProfileUrl").type(STRING).description("투표 생성자 프로필 이미지"),
                            fieldWithPath("data.responseType").type(STRING).description("투표 응답 타입"),
                            fieldWithPath("data.pollType").type(STRING).description("투표 타입"),
                            fieldWithPath("data.endAt").type(STRING).description("투표 종료 시간"),
                            fieldWithPath("data.totalVotingCount").type(NUMBER).description("총 투표 수"),
                            fieldWithPath("data.readCount").type(NUMBER).description("투표 조회 수"),
                            fieldWithPath("data.items").type(ARRAY).description("투표 항목 목록"),
                            fieldWithPath("data.items[].id").type(NUMBER).description("투표 항목 ID"),
                            fieldWithPath("data.items[].description").type(STRING).description("투표 항목 설명"),
                            fieldWithPath("data.items[].imageUrl").type(STRING).description("투표 항목 이미지 URL"),
                            fieldWithPath("data.items[].votingCount").type(NUMBER).description("투표 항목 투표 수")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private GetPollDetailsService.PollDetails mockResult() {
        return new GetPollDetailsService.PollDetails(
            1L,
            "testTitle",
            "testCreatorName",
            "creatorProfileUrl",
            Poll.PollResponseType.SINGLE,
            Poll.PollType.ANONYMOUS,
            LocalDateTime.now(),
            10,
            2,
            List.of(
                new GetPollDetailsService.PollDetails.PollItem(
                    1L,
                    "testDescription",
                    "testImageUrl",
                    5
                ),
                new GetPollDetailsService.PollDetails.PollItem(
                    1L,
                    "testDescription",
                    "testImageUrl",
                    5
                )
            )
        );
    }
}