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
    UNAUTHORIZED("인증되지 않았습니다."),
    INVALID_PARAMETER("파라미터가 올바르지 않습니다."),
    EMPTY_TOKEN("토큰이 없습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    NOT_ACCEPTABLE("허용하지 않는 요청입니다."),
    UNKNOWN("정의 되지 않은 상태입니다."),

    //poll
    POLL_NOT_FOUND("투표를 찾을 수 없습니다."),
    POLL_ITEM_NOT_FOUND("투표 항목을 찾을 수 없습니다."),

    //participation,
    POLL_IS_CLOSED("투표가 종료되었습니다."),
    ALREADY_VOTED("이미 투표하셨습니다."),
    ANONYMOUS_NAME_REQUIRED("익명 투표에는 익명 이름이 필요합니다."),
    INVALID_PARTICIPATION_ITEM_SIZE("투표 항목을 최소 1개이상 선택해주세요."),
    INVALID_SINGLE_PARTICIPATION_ITEM_SIZE("단일 선택 투표에는 1개의 항목만 선택할 수 있습니다."),
    EXCEED_MULTIPLE_PARTICIPATION_ITEM_SIZE("다중 선택 투표에서 선택할 수 있는 최대 항목 갯수를 초과하셨습니다."),
    INVALID_PARTICIPATION_ITEM("투표 항목이 올바르지 않습니다."),
    PARTICIPATION_NOT_FOUND("투표 이력을 찾을 수 없습니다."),
    ANONYMOUS_NOT_CANCEL("비회원은 투표 철회를 할 수 없습니다.")
    ;

    private final String message;

    public static Status from(String text) {
        return Arrays.stream(Status.values())
            .filter(status -> status.name().equals(text))
            .findAny()
            .orElse(UNKNOWN);
    }
}

