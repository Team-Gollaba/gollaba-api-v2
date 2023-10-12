package org.tg.gollaba.participation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.participation.application.DuplicatedParticipationChecker;

import java.util.concurrent.TimeUnit;

import static org.tg.gollaba.common.support.Status.ALREADY_VOTED;

@Component
@RequiredArgsConstructor
public class RedisDuplicatedParticipationChecker implements DuplicatedParticipationChecker {
    private final RedisTemplate<String, String> redisTemplate;
    private final static String VOTE_CHECK_KEY = "vote:check:%s:%d";
    private final static long EXPIRE_HOUR = 24;

    @Override
    public void check(String ipAddress,
                      Long pollId) {
        var value = redisTemplate.opsForValue()
            .get(VOTE_CHECK_KEY.formatted(ipAddress, pollId));

        if (value != null) {
            throw new BadRequestException(ALREADY_VOTED);
        }
    }

    @Override
    public void record(String ipAddress,
                       Long pollId) {
        redisTemplate.opsForValue()
            .set(
                VOTE_CHECK_KEY.formatted(ipAddress, pollId),
                "1",
                EXPIRE_HOUR * 60 * 60,
                TimeUnit.HOURS
            );
    }
}
