package org.tg.gollaba.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.auth.domain.AppleUser;
import org.tg.gollaba.auth.domain.UserToken;

import java.util.Optional;

public interface AppleUserRepository extends JpaRepository<AppleUser, Long> {

    Optional<AppleUserRepository> findByAppleId(String appleId);
}
