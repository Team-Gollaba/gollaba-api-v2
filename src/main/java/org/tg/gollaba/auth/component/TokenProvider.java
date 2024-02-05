package org.tg.gollaba.auth.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.repository.UserTokenRepository;
import org.tg.gollaba.auth.domain.UserToken;
import org.tg.gollaba.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtTokenHandler jwtTokenHandler;

    @Transactional
    public IssuedToken issue(Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtTokenHandler.createAccessToken(user);
        var refreshToken = jwtTokenHandler.createRefreshToken();
        var userToken = new UserToken(user.id(), refreshToken);

        userTokenRepository.save(userToken);

        return new IssuedToken(
            jwtTokenHandler.createAccessToken(user),
            jwtTokenHandler.createRefreshToken()
        );
    }

    public record IssuedToken(
        String accessToken,
        String refreshToken
    ) {
    }
}
