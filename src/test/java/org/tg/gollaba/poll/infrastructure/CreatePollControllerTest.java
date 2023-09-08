package org.tg.gollaba.poll.infrastructure;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.application.CreatePollService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class CreatePollControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("투표 생성");

    @Autowired
    private CreatePollService service;

    @Test
    void success() {
        // given
        Mockito.when(service.create(any()))
            .thenReturn(1L);

        given()
            .body(requestBody()) //바디 추가
            .when()
            .post("/v2/polls")
            .then()
            .log().all()
            .apply(
                document(
                    identifier("save"),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    requestFields(
                        fieldWithPath("title").type(STRING).description("제목"),
                        fieldWithPath("creatorName").type(STRING).description("작성자 이름"),
                        fieldWithPath("responseType").type(STRING).description("응답 타입"),
                        fieldWithPath("pollType").type(STRING).description("투표 타입"),
                        fieldWithPath("endedAt").type(STRING).description("투표 종료 시간"),
                        fieldWithPath("items").type(ARRAY).description("투표 항목"),
                        fieldWithPath("items[].description").type(STRING).description("투표 항목 항목")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.pollId").type(NUMBER).description("투표 ID")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private String requestBody() {
        return """
             {
                 "title": "투표 제목",
                 "creatorName": "투표 생성자 이름",
                 "responseType": "SINGLE",
                 "pollType": "ANONYMOUS",
                 "endedAt": "2021-10-10T10:10:10",
                 "items": [
                     {
                         "description": "탕수육"
                     },
                     {
                         "description": "짜장"
                     }
                 ]
             }
            """;
    }
}