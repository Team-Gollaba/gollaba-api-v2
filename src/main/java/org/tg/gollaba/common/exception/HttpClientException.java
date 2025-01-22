package org.tg.gollaba.common.exception;

public class HttpClientException extends RuntimeException {
    private final int statusCode;

    public HttpClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String message() {
        return super.getMessage();
    }

    public int statusCode() {
        return statusCode;
    }
}
