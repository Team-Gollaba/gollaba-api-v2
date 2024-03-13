package org.tg.gollaba.favorites.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.favorites.domain.Favorites;
import org.tg.gollaba.favorites.repository.FavoritesRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateFavoritesServiceTest {
    @InjectMocks
    private CreateFavoritesService service;
    @Mock
    private FavoritesRepository favoritesRepository;

    @Test
    void success() {
        //given
        var userId = 1L;
        var pollId = 1L;
        given(favoritesRepository.existsByUserIdAndPollId(userId, pollId))
            .willReturn(false);

        //when
        var throwable = catchThrowable(() -> service.create(userId, pollId));

        //then
        assertThat(throwable).isNull();
        var argumentCaptor = ArgumentCaptor.forClass(Favorites.class);
        verify(favoritesRepository).save(argumentCaptor.capture());
        var favorites = argumentCaptor.getValue();
        assertThat(favorites.userId()).isEqualTo(userId);
        assertThat(favorites.pollId()).isEqualTo(pollId);
    }

    @Test
    void 이미_존재하면_스킵() {
        //given
        var userId = 1L;
        var pollId = 1L;
        given(favoritesRepository.existsByUserIdAndPollId(userId, pollId))
            .willReturn(true);

        //when
        var throwable = catchThrowable(() -> service.create(userId, pollId));

        //then
        assertThat(throwable).isNull();
        verify(favoritesRepository, never()).save(any());
    }
}