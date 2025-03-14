package org.tg.gollaba.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.tg.gollaba.common.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CommonExceptionHandler {
    public static final String USER_4XX_MESSAGE = "잘못된 요청입니다.";
    public static final String USER_5XX_MESSAGE = "예상치 못한 오류가 발생하였습니다. 관리자에게 문의해주세요";
    private final ErrorNotificationSender errorNotificationSender;


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public ApiResponse<Void> unAuthorizedException(UnAuthorizedException e,
                                                   HttpServletRequest request) {
        var message = """
            [%s] 인증 정보가 없는 요청이 발생하였습니다.
            """.formatted(request.getRequestURI());
        log.warn(message);
        errorNotificationSender.send(message);

        return ApiResponse.fail(e.message());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> accessDeniedException(AccessDeniedException e,
                                                  HttpServletRequest request) {
        var message = """
            [%s] 권한이 없는 요청이 발생하였습니다.
            """.formatted(request.getRequestURI());
        log.warn(message);
        errorNotificationSender.send(message);

        return ApiResponse.fail("권한이 부족합니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<Void> applicationException(ApplicationException e,
                                                  HttpServletRequest request) {
        log.warn("[{}] 잘못된 요청이 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.error(
            e.status(),
            e.message()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> bindException(BindException e,
                                           HttpServletRequest request) {
        log.warn("[{}] 잘못된 요청이 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.fail(
            e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"))
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class,
    })
    public ApiResponse<Void> handleIllegalException(Exception e,
                                                    HttpServletRequest request) {
        log.warn("[{}] 잘못된 요청이 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        HttpRequestMethodNotSupportedException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        MultipartException.class,
        ConstraintViolationException.class
    })
    public ApiResponse<Void> handle4xx(Exception e,
                                       HttpServletRequest request) {
        log.warn("[{}] 잘못된 요청이 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.fail(USER_4XX_MESSAGE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handle5xx(Exception e,
                                       HttpServletRequest request) {
        log.error("[{}] 예상치 못한 오류가 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.error(USER_5XX_MESSAGE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public ApiResponse<Void> serverException(ServerException e,
                                             HttpServletRequest request) {
        log.error("[{}] 서버 오류가 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return ApiResponse.error(e.message());
    }

    @ExceptionHandler(HttpClientException.class)
    public ResponseEntity<ApiResponse<Void>> serverException(HttpClientException e,
                                                             HttpServletRequest request) {
        log.error("[{}] 외부 서버 오류가 발생하였습니다.", request.getRequestURI(), e);
        errorNotificationSender.send(e);

        return new ResponseEntity<>(ApiResponse.error(e.message()), HttpStatus.valueOf(e.statusCode()));
    }
}
