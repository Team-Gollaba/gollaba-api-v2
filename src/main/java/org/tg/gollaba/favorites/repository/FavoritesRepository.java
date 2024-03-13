package org.tg.gollaba.favorites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.favorites.domain.Favorites;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    boolean existsByUserIdAndPollId(Long userId, Long pollId);
}
