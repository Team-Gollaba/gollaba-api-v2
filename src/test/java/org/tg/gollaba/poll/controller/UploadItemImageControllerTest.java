package org.tg.gollaba.poll.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class UploadItemImageControllerTest extends ControllerTestContext {
    private static final String TAG = ControllerTestContext.Tags.POLL.tagName();
    private static final String DESCRIPTION = ControllerTestContext.Tags.POLL.descriptionWith("항목 이미지 업로드");

    @Test
    @WithMockUser(authorities = "USER")
    void success(){
        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "JWT token")
            .when()
            .post("/v2/polls/{pollHashId}/items/{itemId}/upload-image", testHashId(), 1L)
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
                            | pollHashId | type: String, 설명: 투표 아이디 |
                            | itemId | type: Long, 설명: 항목 아이디 |
                            | image | type: MultipartFile, 설명: 항목 이미지 파일 |  
                            """),
                    preprocessRequest(),
                    preprocessResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                    ),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(STRING).optional().description("이미지 링크")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}