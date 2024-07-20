package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class UpdatePollControllerTest extends ControllerTestContext {
    private static final String TAG = ControllerTestContext.Tags.POLL.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.POLL.descriptionWith("투표 수정");

    @Test
    @WithMockUser(authorities = "USER")
    void success(){
        given()
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .body(requestBody())
            .when()
            .put("/v2/polls/{pollHashId}", testHashId())
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
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                    ),
                    requestFields(
                        fieldWithPath("title").type(STRING).description("투표 제목"),
                        fieldWithPath("endAt").type(STRING).description("투표 종료 시간"),
                        fieldWithPath("items[]").type(ARRAY).description("투표 항목 배열"),
                        fieldWithPath("items[].description").type(STRING).description("투표 항목 설명"),
                        fieldWithPath("items[].imageUrl").type(STRING).description("투표 항목 이미지 URL")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(NULL).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private static Request requestBody(){
        List<UpdatePollControllerTest.Request.Item> items = List.of(
            new UpdatePollControllerTest.Request.Item("항목 설명 1", "이미지 URL 1"),
            new UpdatePollControllerTest.Request.Item("항목 설명 2", "이미지 URL 2")
        );

        return new Request(
            "투표 변경 이름",
            "2024-12-03T10:15:30",
            items
        );
    }

    public record Request(
        String title,
        String endAt,
        List<UpdatePollControllerTest.Request.Item> items
    ) {
        public record Item(
            @NotBlank(message = "투표 항목은 비어있을 수 없습니다.")
            String description,

            @NotBlank(message = "이미지 링크를 삽입해주세요.")
            String imageUrl
        ) {
        }
    }
}