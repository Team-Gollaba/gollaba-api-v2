package org.tg.gollaba.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.user.domain.UserFixture;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenHandlerTest {
    private JwtTokenHandler jwtTokenHandler;
    private static int TEST_ACCESS_EXPIRATION_TIME = 60_000;
    private static int TEST_REFRESH_EXPIRATION_TIME = 120_000;

    @BeforeEach
    void setUp() {
        jwtTokenHandler = new JwtTokenHandler(
            "test",
            TEST_ACCESS_EXPIRATION_TIME,
            TEST_REFRESH_EXPIRATION_TIME
        );
    }
    @Test
    void parseToken() {
        //given
        var token = jwtTokenHandler.createAccessToken(1L);
        var expectedIat = Timestamp.valueOf(LocalDateTime.now()).getTime();
        var expectedExpiration = expectedIat + TEST_ACCESS_EXPIRATION_TIME;

        //when
        var result = jwtTokenHandler.parseToken(token);

        //then
        assertThat(result.get("uid", Long.class)).isEqualTo(1L);
        assertThat(result.get("iss", String.class)).isEqualTo("gollaba");
        assertThat(result.getIssuedAt().getTime()).isBetween(expectedIat - 10000L, expectedIat + 10000L);
        assertThat(result.getExpiration().getTime()).isBetween(expectedExpiration -  10000L, expectedExpiration + 10000L);
    }
}