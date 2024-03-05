package org.tg.gollaba.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.auth.domain.UserToken;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByRefreshToken(String refreshToken);
}
