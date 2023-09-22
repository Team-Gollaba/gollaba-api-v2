package org.tg.gollaba.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.service.PollService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class PollControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.PRODUCT.tagName();
    private static final String DESCRIPTION = Tags.PRODUCT.descriptionWith("투표 생성");

    @Autowired
    private PollService service;

    @Test
    void success() {
        // given
        Mockito.when(service.create(any()))
            .thenReturn(1L);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody())
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
                        fieldWithPath("userId").type(NUMBER).description("유저 아이디"),
                        fieldWithPath("title").type(STRING).description("제목"),
                        fieldWithPath("creatorName").type(STRING).description("작성자 이름"),
                        fieldWithPath("responseType").type(STRING).description("응답 타입"),
                        fieldWithPath("pollType").type(STRING).description("투표 타입"),
                        fieldWithPath("pollItems").type(ARRAY).description("투표 옵션"),
                        fieldWithPath("pollItems[].description").type(STRING).description("투표 옵션 항목"),
                        fieldWithPath("pollItems[].imageUrl").type(STRING).description("투표 옵션 이미지")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                            fieldWithPath("data.pollId").type(NUMBER).description("투표 아이디")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
    private String requestBody(){
        return """
            {
                "userId": 1,
                "title": "title",
                "creatorName": "hamtori",
                "responseType": "SINGLE",
                "pollType": "ANONYMOUS",
                "pollItems": [
                    {
                        "description": "test1",
                        "imageUrl": "link1"
                    },
                            {
                        "description": "test2",
                        "imageUrl": "link2"
                    }
                ]
            }
            """;
    }
}