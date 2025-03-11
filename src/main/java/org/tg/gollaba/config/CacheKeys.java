package org.tg.gollaba.config;

import java.util.List;

public class CacheKeys {
    public static final String TOP_POLLS = "POLL::TOP";
    private static final long TOP_POLLS_EXPIRATION_MIN = 30L;
    public static final String TRENDING_POLLS = "POLL::TRENDING";
    private static final long TRENDING_POLLS_EXPIRATION_MIN = 15L;
    public static final String POLL_DETAILS = "POLL::DETAILS";
    private static final long POLL_DETAILS_EXPIRATION_MIN = 10L;
    public static final String POLL_LIST_HOME = "POLL::LIST::HOME";
    private static final long POLL_LIST_HOME_EXPIRATION_MIN = 20L;

    public static List<CacheKey> getAll() {
        return List.of(
            new CacheKey(TOP_POLLS, TOP_POLLS_EXPIRATION_MIN),
            new CacheKey(TRENDING_POLLS, TRENDING_POLLS_EXPIRATION_MIN),
            new CacheKey(POLL_DETAILS, POLL_DETAILS_EXPIRATION_MIN),
            new CacheKey(POLL_LIST_HOME, POLL_LIST_HOME_EXPIRATION_MIN)
        );
    }

    public record CacheKey(
        String key,
        long expirationMin
    ) {
    }
}
