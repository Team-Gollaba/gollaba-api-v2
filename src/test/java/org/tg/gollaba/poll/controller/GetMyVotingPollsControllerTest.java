package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetMyVotingPollsService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetMyVotingPollsControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("특정 유저가 참여한 투표 전체 조회");

    @Autowired
    private GetMyVotingPollsService getMyVotingPollsService;

    @Test
    @WithMockUser(authorities = "USER")
    void success(){
        when(getMyVotingPollsService.get(any(), any(Pageable.class)))
            .thenReturn(mockResult());

        given()
            .header(authHeader())
            .queryParams("page", 0)
            .queryParams("size", 10)
            .queryParams("sort", "createdAt,desc")
            .when()
            .get("/v2/voting-polls/me")
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
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 크기"),
                        parameterWithName("sort").optional().description("정렬 조건: createAt, endAt")
                    ),
                    requestHeaderWithAuthorization(),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.page").type(NUMBER).description("페이지 번호"),
                            fieldWithPath("data.size").type(NUMBER).description("페이지 크기"),
                            fieldWithPath("data.totalCount").type(NUMBER).description("전체 개수"),
                            fieldWithPath("data.totalPage").type(NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.empty").type(BOOLEAN).description("비어있는지 여부"),
                            fieldWithPath("data.items").type(ARRAY).description("투표 목록"),
                            fieldWithPath("data.items[].id").type(STRING).description("투표 hash ID"),
                            fieldWithPath("data.items[].title").type(STRING).description("투표 제목"),
                            fieldWithPath("data.items[].creatorName").type(STRING).description("투표 생성자"),
                            fieldWithPath("data.items[].creatorProfileUrl").type(STRING).description("투표 생성자 프로필 이미지"),
                            fieldWithPath("data.items[].endAt").type(STRING).description("마감 시간"),
                            fieldWithPath("data.items[].readCount").type(NUMBER).description("조회수"),
                            fieldWithPath("data.items[].totalVotingCount").type(NUMBER).description("총 투표 수"),
                            fieldWithPath("data.items[].votedPeopleCount").type(NUMBER).description("투표한 사람 수"),
                            enumDescription(
                                fieldWithPath("data.items[].responseType").type(STRING).description("응답 타입"),
                                Poll.PollResponseType.class
                            ),
                            enumDescription(
                                fieldWithPath("data.items[].pollType").type(STRING).description("투표 타입"),
                                Poll.PollType.class
                            ),
                            fieldWithPath("data.items[].items").type(ARRAY).description("투표 항목"),
                            fieldWithPath("data.items[].items[].id").type(NUMBER).description("투표 항목 ID"),
                            fieldWithPath("data.items[].items[].description").type(STRING).description("투표 항목 설명"),
                            fieldWithPath("data.items[].items[].imageUrl").type(STRING).description("투표 항목 이미지 URL"),
                            fieldWithPath("data.items[].items[].votingCount").type(NUMBER).description("투표 항목 투표수")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private Page<PollSummary> mockResult() {
        List<PollSummary> pollSummaries = List.of(
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

        return new PageImpl<>(
            pollSummaries,
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
            1
        );
    }
}