package org.tg.gollaba.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import io.restassured.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.auth.service.RenewTokenService;
import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.tg.gollaba.common.ApiDocumentUtils.fieldsWithBasic;

class RenewTokenControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.AUTHORIZATION.tagName();
    private static final String DESCRIPTION = Tags.AUTHORIZATION.descriptionWith("토큰 갱신");
    @Autowired
    private RenewTokenService renewTokenService;

    @Test
    void success() {
        when(renewTokenService.renew("refreshToken"))
            .thenReturn("accessToken");

        given()
            .cookie(new Cookie.Builder(REFRESH_TOKEN, "refreshToken").build())
            .when()
            .post("/v2/auth/renew-token")
            .then()
            .log().all()
            .apply(
                document(
                    identifier(),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .description(DESCRIPTION),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                            fieldWithPath("data.accessToken").type(STRING).description("갱신된 액세스 토큰")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}