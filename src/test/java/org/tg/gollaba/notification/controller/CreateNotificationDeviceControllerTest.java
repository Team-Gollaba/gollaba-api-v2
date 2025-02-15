package org.tg.gollaba.notification.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.notification.domain.NotificationDevice;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class CreateNotificationDeviceControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.NOTIFICATION.tagName();
    private static final String DESCRIPTION = Tags.NOTIFICATION.descriptionWith("디바이스 등록");

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        given()
            .body(requestBody())
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .post("/v2/app-notifications")
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
                        fieldWithPath("agentId").type(STRING).description("에이전트 아이디"),
                        fieldWithPath("osType").type(STRING).description("운영체제 타입"),
                        fieldWithPath("deviceName").type(STRING).description("디바이스 이름"),
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

    private CreateNotificationDeviceController.Request requestBody() {
        return new CreateNotificationDeviceController.Request(
            "agentId",
            NotificationDevice.OperatingSystemType.IOS,
            "deviceName",
            true
        );
    }
}

