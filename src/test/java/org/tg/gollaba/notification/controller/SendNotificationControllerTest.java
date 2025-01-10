package org.tg.gollaba.notification.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class SendNotificationControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.NOTIFICATION.tagName();
    private static final String DESCRIPTION = Tags.NOTIFICATION.descriptionWith("푸쉬 메시지 전송");

    @Test
    void success() {
        given()
            .body(requestBody())
            .post("/v2/server-message")
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
                    requestFields(
                        fieldWithPath("title").type(STRING).description("푸쉬 알림 제목"),
                        fieldWithPath("content").type(STRING).description("푸쉬 알림 내용")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(null).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }

    private SendNotificationController.Request requestBody() {
        return new SendNotificationController.Request(
            "title",
            "content"
        );
    }
}

