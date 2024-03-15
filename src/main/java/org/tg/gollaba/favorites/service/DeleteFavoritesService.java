package org.tg.gollaba.favorites.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.favorites.repository.FavoritesRepository;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class DeleteFavoritesService {
    private final FavoritesRepository favoritesRepository;

    @Transactional
    public void delete(Long userId, Long favoritesId){
        var favorites = favoritesRepository.findById(favoritesId)
            .orElseThrow(() -> new BadRequestException(FAVORITE_NOT_FOUND));

        if(!favoritesRepository.existsByUserId(userId)){
            throw new BadRequestException(INVALID_FAVORITE_OPERATION);
        }

        favoritesRepository.delete(favorites);
    }
}
