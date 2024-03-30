package org.tg.gollaba.common.web;

import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;

public abstract class HashIdController {
    private final HashIdHandler hashIdHandler;

    protected HashIdController(HashIdHandler hashIdHandler) {
        this.hashIdHandler = hashIdHandler;
    }

    protected String createHashId(Long pollId) {
        return hashIdHandler.encode(pollId);
    }

    protected long getPollId(String pollHashId) {
        long result;

        try {
            result = hashIdHandler.decode(pollHashId);
        } catch (HashIdHandler.FailToDecodeHashIdException e) {
            throw new BadRequestException(Status.INVALID_POLL_HASH_ID);
        }

        return result;
    }
}
