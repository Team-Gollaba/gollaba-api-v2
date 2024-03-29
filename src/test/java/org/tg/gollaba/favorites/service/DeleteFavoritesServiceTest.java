package org.tg.gollaba.favorites.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.favorites.domain.FavoritesFixture;
import org.tg.gollaba.favorites.repository.FavoritesRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteFavoritesServiceTest {
    @InjectMocks
    private DeleteFavoritesService service;
    @Mock
    private FavoritesRepository favoritesRepository;

    @Test
    void success() {
        //given
        var userId = 1L;
        var pollId = 1L;
        var favorites = new FavoritesFixture()
            .setUserId(userId)
            .setPollId(pollId)
            .build();
        given(favoritesRepository.findByUserIdAndPollId(userId, pollId))
            .willReturn(Optional.of(favorites));

        //when
        var throwable = catchThrowable(() -> service.delete(userId, pollId));

        //then
        assertThat(throwable).isNull();
        verify(favoritesRepository, times(1)).delete(favorites);
    }

    @Test
    void 이미_삭제됐거나_없으면_삭제안함(){
        //given
        given(favoritesRepository.findByUserIdAndPollId(any(), any()))
            .willReturn(Optional.empty());

        //when
        var throwable = catchThrowable(() -> service.delete(1L, 1L));

        //then
        assertThat(throwable).isNull();
        verify(favoritesRepository, never()).delete(any());
    }
}