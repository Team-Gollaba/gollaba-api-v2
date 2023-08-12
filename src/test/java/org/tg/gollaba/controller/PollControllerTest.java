package org.tg.gollaba.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.dto.PollDto;
import org.tg.gollaba.service.PollService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;

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
        List<PollService.PollOptionRequirement> pollOptionRequirements = new ArrayList<>();
        pollOptionRequirements.add(new PollService.PollOptionRequirement("test", "imgUrl"));
        pollOptionRequirements.add(new PollService.PollOptionRequirement("test2", "imgUrl2"));

        var createRequest = new PollService.CreateRequirement(
            Optional.ofNullable(1L),
            "title",
            "hamtori",
            Poll.PollType.NAMED,
            Poll.PollResponseType.MULTI,
            pollOptionRequirements);


        Mockito.when(service.create(createRequest))
            .thenReturn(mockResult()); //dto가 들어와야 함

        given()
            .contentType(ContentType.JSON) //보내는 방식 추가 ..
            .body(createRequest) //바디 추가
            .when()
            .post(
                "/v2/polls"
            )
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
                        fieldWithPath("pollOptions").type(ARRAY).description("투표 옵션"),
                        fieldWithPath("pollOptions[].description").type(STRING).description("투표 옵션 항목"),
                        fieldWithPath("pollOptions[].imageUrl").type(STRING).description("투표 옵션 이미지")
                        //코파일럿?
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                            fieldWithPath("data.pollId").type(NUMBER).description("투표 ID")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private PollDto mockResult() {
        return new PollDto(
            1L,
            1L,
            "title",
            "creatorName",
            Poll.PollResponseType.MULTI,
            Poll.PollType.NAMED,
            LocalDateTime.now(),
            0
        );
    }
}