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
    public void create(Long userId, Long pollId) {
        if (favoritesRepository.existsByUserIdAndPollId(userId, pollId)) {
            return;
        }

        var favorites = new Favorites(userId, pollId);
        favoritesRepository.save(favorites);
    }
}
