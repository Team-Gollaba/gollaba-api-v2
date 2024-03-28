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
    public void delete(Long userId, Long pollId){
        favoritesRepository.findByUserIdAndPollId(userId, pollId)
            .ifPresent(favoritesRepository::delete);
    }
}
