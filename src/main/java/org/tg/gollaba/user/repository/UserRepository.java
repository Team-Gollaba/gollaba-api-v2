package org.tg.gollaba.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndProviderId(String email,
                                       String providerId);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProviderId(String email,
                                            String providerId);

    boolean existsByProviderIdAndProviderType(String providerId,
                                              User.ProviderType providerType);
    Optional<User> findByProviderIdAndProviderType(String providerId,
                                                   User.ProviderType providerType);

}
