package org.tg.gollaba.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.auth.component.JwtTokenProvider;
import org.tg.gollaba.auth.component.TokenProvider;
import org.tg.gollaba.auth.repository.UserTokenRepository;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;

@Service
@RequiredArgsConstructor
public class RenewTokenService {
    private final JwtTokenHandler jwtTokenHandler;
    private final TokenProvider tokenProvider;
    private final UserTokenRepository userTokenRepository;

    @Transactional
    public String renew(String refreshToken) {
        validateToken(refreshToken);

        var userToken = userTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new BadRequestException(Status.NOT_EXISTS_REFRESH_TOKEN));

        return tokenProvider.issue(userToken.userId())
            .accessToken();
    }

    private void validateToken(String refreshToken) {
        try {
            jwtTokenHandler.parseToken(refreshToken);
        } catch (Exception e) {
            throw new BadRequestException(
                Status.INVALID_TOKEN,
                Status.INVALID_TOKEN.message() + " : " + e.getMessage()
            );
        }
    }
}
