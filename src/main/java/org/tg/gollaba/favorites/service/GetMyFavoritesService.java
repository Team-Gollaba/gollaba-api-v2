package org.tg.gollaba.favorites.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.favorites.domain.Favorites;
import org.tg.gollaba.favorites.repository.FavoritesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyFavoritesService {
    private final FavoritesRepository favoritesRepository;

    public List<FavoritesSummary> get(Long userId){
        var favoritesList = favoritesRepository.findAllByUserId(userId);

        return favoritesList.stream()
            .map(FavoritesSummary::from)
            .toList();
    }

    public record FavoritesSummary(
        Long id,
        Long userId,
        Long pollId
    ){
        public static FavoritesSummary from(Favorites favorites){
            return new FavoritesSummary(
                favorites.id(),
                favorites.userId(),
                favorites.pollId()
            );
        }
    }
}
