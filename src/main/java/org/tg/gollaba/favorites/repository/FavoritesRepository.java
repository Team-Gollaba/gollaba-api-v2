package org.tg.gollaba.favorites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.favorites.domain.Favorites;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Optional<Favorites> findByUserIdAndPollId(Long userId, Long pollId);

    boolean existsByUserId(Long userId);
}
