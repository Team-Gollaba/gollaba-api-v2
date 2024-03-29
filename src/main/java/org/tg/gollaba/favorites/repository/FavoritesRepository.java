package org.tg.gollaba.favorites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tg.gollaba.favorites.domain.Favorites;

import java.util.List;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Optional<Favorites> findByUserIdAndPollId(Long userId, Long pollId);

    List<Favorites> findAllByUserId(Long userId);
}
