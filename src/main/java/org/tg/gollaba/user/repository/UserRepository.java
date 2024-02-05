package org.tg.gollaba.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndProviderId(String email,
                                       String providerId);

    Optional<User> findByEmailAndProviderId(String email,
                                            String providerId);

}
