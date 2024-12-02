package org.tg.gollaba.auth.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.auth.domain.UserToken;
import org.tg.gollaba.auth.repository.UserTokenRepository;
import org.tg.gollaba.auth.vo.IssuedToken;
import org.tg.gollaba.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtTokenHandler jwtTokenHandler;

    @Transactional
    @Override
    public IssuedToken issue(Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtTokenHandler.createAccessToken(user.id());
        var refreshToken = jwtTokenHandler.createRefreshToken();
        var userToken = new UserToken(user.id(), refreshToken);

        userTokenRepository.save(userToken);

        return new IssuedToken(
            accessToken,
            refreshToken
        );
    }
}
