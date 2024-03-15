package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class GetPollListControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("투표 목록 조회");
    @Autowired
    private GetPollListService getPollListService;

    @Test
    void success() {
        when(getPollListService.get(any()))
            .thenReturn(mockResult());

        given()
            .param("optionGroup", "TITLE")
            .param("query", "query")
            .param("isActive", true)
            .param("pollType", "NAMED")
            .param("page", 0)
            .param("size", 10)
            .param("sort", "createdAt,desc")
            .when()
            .get("/v2/polls")
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
                        parameterWithName("sort").optional().description("정렬 조건: createAt, endAt"),
                        enumDescription(
                            parameterWithName("pollType").optional().description("투표 타입"),
                            Poll.PollType.class
                        ),
                        enumDescription(
                            parameterWithName("optionGroup").optional().description("옵션 그룹: TITLE"),
                            GetPollListService.OptionGroup.class
                        ),
                        parameterWithName("query").optional().description("검색어"),
                        parameterWithName("isActive").optional().description("활성화 여부")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.page").type(NUMBER).description("페이지 번호"),
                            fieldWithPath("data.size").type(NUMBER).description("페이지 크기"),
                            fieldWithPath("data.totalCount").type(NUMBER).description("전체 개수"),
                            fieldWithPath("data.totalPage").type(NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.empty").type(BOOLEAN).description("비어있는지 여부"),
                            fieldWithPath("data.items").type(ARRAY).description("투표 목록"),
                            fieldWithPath("data.items[].id").type(NUMBER).description("투표 ID"),
                            fieldWithPath("data.items[].title").type(STRING).description("투표 제목"),
                            fieldWithPath("data.items[].creatorName").type(STRING).description("투표 생성자"),
                            fieldWithPath("data.items[].endedAt").type(STRING).description("마감 시간"),
                            fieldWithPath("data.items[].readCount").type(NUMBER).description("조회수"),
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
                            fieldWithPath("data.items[].items[].voteCount").type(NUMBER).description("투표 항목 투표수")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private PageImpl<GetPollListService.PollSummary> mockResult() {
        return new PageImpl<>(
            List.of(
                new GetPollListService.PollSummary(
                    1L,
                    "title",
                    "creatorName",
                    Poll.PollResponseType.SINGLE,
                    Poll.PollType.NAMED,
                    LocalDateTime.now(),
                    0,
                    List.of(
                        new GetPollListService.PollSummary.PollItem(
                            1L,
                            "description",
                            "imageUrl",
                            0
                        )
                    )
                )
            ),
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
            1
        );
    }
}