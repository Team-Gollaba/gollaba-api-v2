package org.tg.gollaba.image.controller;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.tg.gollaba.common.ControllerTestContext;
import org.tg.gollaba.image.service.UploadImageService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.tg.gollaba.common.ApiDocumentUtils.*;

class UploadImageControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.IMAGE.name();

    @Autowired
    private UploadImageService uploadImageService;
    @TempDir
    File tempDir;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        // 임시 파일 생성
        tempFile = new File(tempDir, "testFile.txt");
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
    }

    @Test
    void uploadImage() {
        BDDMockito.when(uploadImageService.upload(any()))
            .thenReturn(List.of("s3Url"));

        given()
            .multiPart("filePath", "test/path")
            .multiPart("files", tempFile)
            .post("/v2/image/upload")
            .then()
            .log()
            .all()
            .apply(
                document(
                    identifier("uploadImage"),
                    new ResourceSnippetParametersBuilder()
                        .tag(TAG)
                        .summary("이미지 업로드")
                        .description("""
                           | content-type |
                           |------|
                           | multipart/form-data |
                           
                           
                           | 항목 | type | description |
                           |------|-------------|------------|
                           | filePath | String | 파일 경로 (S3 경로) !!맨앞에 "/" 붙히지 마시오 |
                           | files | File[] | 이미지 파일 리스트 |
                            """),
                    preprocessRequest(),
                    preprocessResponse(),
                    responseFields(
                        fieldsWithBasic(
                            fieldWithPath("data").type(ARRAY).description("저장된 URL 리스트")
                        )
                    )
                )
            )
            .status(HttpStatus.OK);
    }
}