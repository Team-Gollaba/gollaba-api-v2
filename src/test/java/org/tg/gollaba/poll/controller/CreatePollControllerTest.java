package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.CreatePollService;
import org.tg.gollaba.user.controller.CreateUserController;
import org.tg.gollaba.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CreatePollControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POLL.tagName();
    private static final String DESCRIPTION = Tags.POLL.descriptionWith("생성");
    @Autowired
    private CreatePollService service;

    @Test
    void success() {
        when(service.create(any()))
            .thenReturn(1L);

        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(Map.of(
                "title", "testTitle",
                "creatorName", "testCreatorName",
                "responseType", Poll.PollResponseType.MULTIPLE.name(),
                "pollType", Poll.PollType.NAMED.name(),
                "endAt", LocalDateTime.now().toString(),
                "items[0].description", "testDescription"
            ))
            .when()
            .post("/v2/polls")
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
                            | creatorName | type: String, 설명: 생성자 이름 |
                            | responseType | type: Enum, 설명: 응답 타입 (SINGLE, MULTIPLE) |
                            | pollType | type: Enum, 설명: 투표 타입 (NAMED, ANONYMOUS) |
                            | endAt | type: DateTime, 설명: 투표 종료 시간 |
                            | items | type: List, 설명: 투표 항목 |
                            | items[].description | type: String, 설명: 투표 항목 설명 |
                            | items[].image | type: MultipartFile, 설명: 투표 항목 이미지 |
                            """),
                    preprocessRequest(),
                    preprocessResponse(),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(NUMBER).description("생성된 투표 ID")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}