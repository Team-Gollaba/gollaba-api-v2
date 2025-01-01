package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.service.GetPollSearchedHistoryService;

import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

public class GetPollSearchedHistoryControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("인기 검색어 조회");

    @Autowired
    private GetPollSearchedHistoryService service;

    @Test
    void success(){
        when(service.get())
            .thenReturn(mockResult());

        given()
            .when()
            .get("/v2/polls/search-trending")
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
                            fieldWithPath("data[].searchedWord").type(STRING).description("검색어"),
                            fieldWithPath("data[].searchCount").type(NUMBER).description("검색된 횟수")
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
                    "searchedWord", "검색어1",
                    "searchCount", 2
                ),
                Map.of(
                    "searchedWord", "검색어2",
                    "searchCount", 5
                )
            )
        );
    }
}