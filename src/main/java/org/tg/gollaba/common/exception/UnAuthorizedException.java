package org.tg.gollaba.common.exception;

import org.tg.gollaba.common.support.Status;

public class UnAuthorizedException extends BadRequestException {
    public UnAuthorizedException() {
        super(Status.UNAUTHORIZED);
    }
}
