package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class UpdatePollControllerTest extends ControllerTestContext {

    private static final String TAG = ControllerTestContext.Tags.POLL.tagName();

    private static final String DESCRIPTION = ControllerTestContext.Tags.POLL.descriptionWith("회원 수정");

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            //.formParam("name", "testName")
            .when()
            .post("/v2/polls/{hashPollId}/update", "hashPollId")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .summary(DESCRIPTION)
                        .tag(TAG)
                        .description("""
                            | 항목 | description |
                            |------|-------------|
                            | title | type: String, 설명: 투표 제목 |
                            | endAt | type: DateTime, 설명: 투표 종료 시간 |
                            | items | type: List, 설명: 투표 항목 |
                            | items[].description | type: String, 설명: 투표 항목 설명 |
                            | items[].image | type: MultipartFile, 설명: 투표 항목 이미지 |
                            """),
                    preprocessRequest(),
                    preprocessResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
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
}