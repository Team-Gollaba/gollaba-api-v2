package org.tg.gollaba.notification.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.notification.service.GetUserNotificationListService;
import org.tg.gollaba.notification.vo.AppNotificationVo;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

public class GetUserNotificationListControllerTest extends ControllerTestContext {

    private static final String TAG = ControllerTestContext.Tags.NOTIFICATION.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.NOTIFICATION.descriptionWith("유저 디바이스 알림 전체 조회");

    @Autowired
    private GetUserNotificationListService service;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        when(service.get(any(), any(Pageable.class)))
            .thenReturn(mockResult());
        given()
            .header(authHeader())
            .queryParam("page", 0)
            .queryParam("size", 10)
            .queryParams("sort", "createdAt,desc")
            .when()
            .get("/v2/app-notifications")
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
                    queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 크기"),
                        parameterWithName("sort").optional().description("정렬 조건: createdAt,desc || createdAt,asc")
                    ),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                    ),
                    responseFields(
                        fieldsWithBasic(
                        fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                        fieldWithPath("data.items").type(ARRAY).description("알림 목록"),
                        fieldWithPath("data.items[].notificationId").type(NUMBER).description("알림 ID"),
                        fieldWithPath("data.items[].userId").type(NUMBER).description("사용자 ID"),
                        fieldWithPath("data.items[].deepLink").type(STRING).description("딥링크"),
                        fieldWithPath("data.items[].title").type(STRING).description("알림 제목"),
                        fieldWithPath("data.items[].content").type(STRING).description("알림 내용"),
                        fieldWithPath("data.page").type(NUMBER).description("페이지 번호"),
                        fieldWithPath("data.size").type(NUMBER).description("페이지 크기"),
                        fieldWithPath("data.totalCount").type(NUMBER).description("전체 개수"),
                        fieldWithPath("data.totalPage").type(NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.empty").type(BOOLEAN).description("비어있는지 여부")
                        )
                    )
                )
            )
            .statusCode(HttpStatus.OK.value());
    }

    private Page<AppNotificationVo> mockResult() {
        List<AppNotificationVo> appNotificationVoList = List.of(
            new AppNotificationVo(
                1L,
                1L,
                "https://deepLink",
                "알림 제목 1",
                "알림 내용 1"
            ),
            new AppNotificationVo(
                2L,
                2L,
                "https://deepLink2",
                "알림 제목 2",
                "알림 내용 2"
            )
        );

        return new PageImpl<>(
            appNotificationVoList,
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
            appNotificationVoList.size()
        );
    }
}