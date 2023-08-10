package org.tg.gollaba.controller;

import org.tg.gollaba.common.ControllerTestContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

class PollControllerTest extends ControllerTestContext {
//    private static final String TAG = Tags.PRODUCT.tagName();
//    private static final String DESCRIPTION = Tags.PRODUCT.descriptionWith("상품 상세 조회");
//
//    @Autowired
//    private PollService service;
//
//    @Test
//    void success() {
//        Mockito.when(service.findById(eq(1L)))
//            .thenReturn(mockResult());
//
//        given()
//            .when()
//            .get(
//                "/v1/products/{productId}",
//                1L
//            )
//            .then()
//            .log().all()
//            .apply(
//                document(
//                    identifier("findById"),
//                    new ResourceSnippetParametersBuilder()
//                        .tag(TAG)
//                        .description(DESCRIPTION),
//                    responseFields(
//                        fieldsWithBasic(
//                            fieldWithPath("data").type(OBJECT).description("결과 데이터"),
//                            fieldWithPath("data.id").type(NUMBER).description("상품 ID"),
//                            fieldWithPath("data.name").type(STRING).description("상품 이름")
//                        )
//                    )
//                )
//            )
//            .status(HttpStatus.OK);
//    }
//
//    private PollVo mockResult() {
//        return new PollVo(
//            1L,
//            "testName"
//        );
//    }
}