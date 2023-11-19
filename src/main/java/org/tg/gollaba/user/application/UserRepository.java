package org.tg.gollaba.user.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
}
