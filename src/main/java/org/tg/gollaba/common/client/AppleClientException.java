package org.tg.gollaba.common.client;

import org.tg.gollaba.common.exception.HttpClientException;

public class AppleClientException extends HttpClientException {
    private static final String BASE_MESSAGE = "애플 서버와의 통신에 실패했습니다. ";

    public AppleClientException(int statusCode) {
        super(statusCode, BASE_MESSAGE + "관리자에게 문의해주세요.");
    }

    public AppleClientException(int statusCode, String message) {
        super(statusCode, BASE_MESSAGE + message);
    }
}