package org.tg.gollaba.report.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.report.domain.PollReport;
import org.tg.gollaba.report.service.ReportService;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static javax.lang.model.element.ElementKind.ENUM;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class ReportControllerTest extends ControllerTestContext {

    private static final String TAG = Tags.REPORT.tagName();
    private static final String DESCRIPTION = Tags.REPORT.descriptionWith("투표 신고");

    @Autowired
    private ReportService reportService;

    @Test
    @WithMockUser(authorities = "USER")
    void success() {
        doNothing().when(reportService).report(requirement());

        given()
            .body(requestBody())
            .header(authHeader())
            .when()
            .post("/v2/polls/{pollHashId}/reports", testHashId())
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
                        enumDescription(
                            fieldWithPath("reportType").type(ENUM).description("신고 유형"),
                            PollReport.ReportType.class
                        ),
                        fieldWithPath("content").type(STRING).description("신고 내용")
                    ),
                    requestHeaderWithAuthorization(),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(null).description("응답 데이터")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
        }
        private ReportController.Request requestBody(){
            return new ReportController.Request(
                "content",
                PollReport.ReportType.SPAM
            );
        }

        private ReportService.Requirement requirement(){
        return new ReportService.Requirement(
            1L,
            "ipAddress||userId",
            PollReport.ReporterType.REGISTERED_USER,
            PollReport.ReportType.SPAM,
            "content"
        );
        }
    }