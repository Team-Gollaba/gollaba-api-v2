package org.tg.gollaba.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    boolean existsByEmailAndProviderId(String email,
                                       String providerId);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProviderId(String email,
                                            String providerId);

    boolean existsByProviderIdAndProviderType(String providerId,
                                              User.ProviderType providerType);
    Optional<User> findByProviderIdAndProviderType(String providerId,
                                                   User.ProviderType providerType);

    @Query("SELECT u.profileImageUrl FROM User u WHERE u.id = :userId")
    Optional<String> findProfileImageUrlByUserId(@Param("userId") Long userId);

}
