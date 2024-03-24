package org.tg.gollaba.favorites.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.favorites.domain.Favorites;
import org.tg.gollaba.favorites.repository.FavoritesRepository;

@Service
@RequiredArgsConstructor
public class CreateFavoritesService {
    private final FavoritesRepository favoritesRepository;

    @Transactional
    public Long create(Long userId, Long pollId) {
        return favoritesRepository.findByUserIdAndPollId(userId, pollId)
            .map(Favorites::id)
            .orElseGet(() -> createFavorites(userId, pollId).id());
    }

    private Favorites createFavorites(Long userId, Long pollId) {
        var favorites = new Favorites(userId, pollId);

        return favoritesRepository.save(favorites);
    }
}