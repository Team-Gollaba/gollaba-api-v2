package org.tg.gollaba.notification.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.tg.gollaba.common.ApiDocumentUtils.*;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class UpdateDeviceSendNotificationControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.NOTIFICATION.tagName();
    private static final String DESCRIPTION = Tags.NOTIFICATION.descriptionWith("디바이스 알림 수정");

    @Test
    void success() {
        given()
            .header("Authorization", "Bearer fcm-token")
            .body(requestBody())
            .when()
            .put("/v2/app-notifications")
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
                    requestHeaderWithAuthorization(),
                    requestFields(
                        fieldWithPath("userId").type(NUMBER).description("유저 아이디"),
                        fieldWithPath("agentId").type(STRING).description("에이전트 아이디"),
                        fieldWithPath("allowsNotification").type(BOOLEAN).description("알림 허용 여부")
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

    private UpdateDeviceNotificationController.Request requestBody() {
        return new UpdateDeviceNotificationController.Request(
            1L,
            "agentId",
            true
        );
    }
}