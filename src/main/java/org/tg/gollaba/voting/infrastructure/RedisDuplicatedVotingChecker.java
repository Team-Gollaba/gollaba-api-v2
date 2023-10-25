package org.tg.gollaba.voting.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.application.DuplicatedVotingChecker;

import java.util.concurrent.TimeUnit;

import static org.tg.gollaba.common.support.Status.ALREADY_VOTING;

@Component
@RequiredArgsConstructor
public class RedisDuplicatedVotingChecker implements DuplicatedVotingChecker {
    private final RedisTemplate<String, String> redisTemplate;
    private final static String VOTING_CHECK_KEY = "voting:check:%s:%d";
    private final static long EXPIRE_HOUR = 24;

    @Override
    public void check(String ipAddress,
                      Long pollId) {
        var value = redisTemplate.opsForValue()
            .get(VOTING_CHECK_KEY.formatted(ipAddress, pollId));

        if (value != null) {
            throw new BadRequestException(ALREADY_VOTING);
        }
    }

    @Override
    public void record(String ipAddress,
                       Long pollId) {
        redisTemplate.opsForValue()
            .set(
                VOTING_CHECK_KEY.formatted(ipAddress, pollId),
                "1",
                EXPIRE_HOUR * 60 * 60,
                TimeUnit.HOURS
            );
    }
}
