package org.tg.gollaba.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.auth.component.JwtTokenProvider;
import org.tg.gollaba.auth.domain.UserTokenFixture;
import org.tg.gollaba.auth.repository.UserTokenRepository;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RenewTokenServiceTest {
    @InjectMocks
    private RenewTokenService service;
    @Mock
    private JwtTokenHandler jwtTokenHandler;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserTokenRepository userTokenRepository;

    @Test
    void success() {
        //given
        var refreshToken = "refreshToken";
        var userToken = new UserTokenFixture().build();
        var issuedToken = new JwtTokenProvider.IssuedToken("accessToken", "refreshToken");
        given(userTokenRepository.findByRefreshToken(refreshToken))
            .willReturn(Optional.of(userToken));
        given(tokenProvider.issue(userToken.userId()))
            .willReturn(issuedToken);

        //when
        var throwable = catchThrowable(() -> service.renew(refreshToken));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void fail() {
        //given
        var refreshToken = "refreshToken";
        given(jwtTokenHandler.parseToken(refreshToken))
            .willThrow(new RuntimeException("error"));

        //when
        var throwable = catchThrowable(() -> service.renew(refreshToken));

        //then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class)
            .hasMessageContaining(Status.INVALID_TOKEN.message());
    }
}