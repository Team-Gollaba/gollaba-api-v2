package org.tg.gollaba.common.client;

import org.tg.gollaba.common.exception.HttpClientException;

public class FcmClientException extends HttpClientException {
    private static final String BASE_MESSAGE = "파이어베이스 서버와의 통신에 실패했습니다. ";

    public FcmClientException(int statusCode) {
        super(statusCode, BASE_MESSAGE + "관리자에게 문의해주세요.");
    }

    public FcmClientException(int statusCode, String message) {
        super(statusCode, BASE_MESSAGE + message);
    }
}