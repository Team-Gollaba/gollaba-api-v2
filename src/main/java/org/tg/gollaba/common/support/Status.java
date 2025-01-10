package org.tg.gollaba.common.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum Status {
    //common
    SUCCESS("성공"),
    ERROR("에러"),
    FAIL("요청을 처리할 수 없습니다."),
    NOT_FOUND("리소스를 찾을 수 없습니다."),
    UNAUTHORIZED("인증 정보가 부족합니다. 인증 정보를 확인해주세요."),
    INVALID_PARAMETER("파라미터가 올바르지 않습니다."),
    EMPTY_TOKEN("토큰이 없습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    NOT_EXISTS_REFRESH_TOKEN("리프레시 토큰이 DB에 존재하지 않습니다."),
    NOT_ACCEPTABLE("허용하지 않는 요청입니다."),
    UNKNOWN("정의 되지 않은 상태입니다."),
    FAIL_TO_UPLOAD("파일 업로드에 실패했습니다."),
    INVALID_POLL_HASH_ID("올바르지 않은 투표 ID 입니다."),
    FAIL_TO_SQL("SQL 처리에 실패했습니다."),
    IMAGE_INVALID_FILE("올바른 이미지 파일이 아닙니다."),

    //poll
    POLL_NOT_FOUND("투표를 찾을 수 없습니다."),
    POLL_ITEM_NOT_FOUND("투표 항목을 찾을 수 없습니다."),
    POLL_TYPE_NOT_NAMED("기명 투표가 아닙니다"),

    //voting
    POLL_IS_CLOSED("투표가 종료되었습니다."),
    ALREADY_VOTING("이미 투표하셨습니다."),
    ANONYMOUS_NAME_REQUIRED("익명 투표에는 익명 이름이 필요합니다."),
    INVALID_VOTING_ITEM_SIZE("투표 항목을 최소 1개이상 선택해주세요."),
    INVALID_SINGLE_VOTING_ITEM_SIZE("단일 선택 투표에는 1개의 항목만 선택할 수 있습니다."),
    INVALID_VOTING_ITEM("투표 항목이 올바르지 않습니다."),
    VOTING_NOT_FOUND("투표 이력을 찾을 수 없습니다."),
    IMAGE_EXCESS("이미지는 12개 이하로 제한됩니다."),
    VOTING_NOT_USER("본인의 투표만 수정할 수 있습니다."),
    VOTING_ALREADY_ENDED("이미 종료된 투표는 수정할 수 없습니다."),

    //favorites
    FAVORITE_NOT_FOUND("좋아요 이력을 찾을 수 없습니다."),
    INVALID_FAVORITE_OPERATION("좋아요를 요청한 사용자가 아닙니다."),

    //user
    INCONSISTENCY_PASSWORD("일치하지 않는 비밀번호입니다."),
    USER_NOT_FOUND("존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION("이미 존재하는 이메일입니다."),
    NOT_SIGN_UP("회원가입을 진행해주세요."),

    //external
    KAKAO("카카오 서버와의 통신에 실패하였습니다. 관리자에게 문의해주세요."),
    FAIL_TO_SEND_FCM_MESSAGE("FCM 메시지 전송에 실패했습니다."),

    //notification
    APP_NOTIFICATION_NOT_FOUND("등록된 기기를 찾을 수 없습니다"),
    DUPLICATE_DEVICE("이미 등록된 디바이스 입니다");

    private final String message;

    public static Status from(String text) {
        return Arrays.stream(Status.values())
            .filter(status -> status.name().equals(text))
            .findAny()
            .orElse(UNKNOWN);
    }
}

