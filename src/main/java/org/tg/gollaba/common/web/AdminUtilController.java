package org.tg.gollaba.common.web;

import org.tg.gollaba.common.exception.BadRequestException;

import static org.tg.gollaba.common.support.Status.INVALID_PARAMETER;

public abstract class AdminUtilController {
    private final String adminKey;

    protected AdminUtilController(String adminKey) {
        this.adminKey = adminKey;
    }

    protected void validate(String requestAdminKey) {
        if (!adminKey.equals(requestAdminKey)) {
            throw new BadRequestException(INVALID_PARAMETER, "잘못된 adminKey 입니다.");
        }
    }
}
