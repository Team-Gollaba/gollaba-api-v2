package org.tg.gollaba.poll.component;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;

@Component
public class HashIdHandler {
    private final Hashids hashids;

    public HashIdHandler(@Value("${security.hash-ids.salt}") String salt,
                         @Value("${security.hash-ids.min-length}") int minLength) {
        this.hashids = new Hashids(salt, minLength);
    }

    public String encode(long id) {
        return hashids.encode(id);
    }

    public long decode(String hash) {
        long[] result = hashids.decode(hash);

        if (result.length < 1) {
            throw new ServerException(Status.FAIL_TO_DECODE_HASH_ID);
        }

        return result[0];
    }
}
