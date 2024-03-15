package org.tg.gollaba.favorites.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.favorites.domain.FavoritesFixture;
import org.tg.gollaba.favorites.repository.FavoritesRepository;
import org.tg.gollaba.user.domain.UserFixture;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeleteFavoritesServiceTest {
    @InjectMocks
    private DeleteFavoritesService service;

    @Mock
    private FavoritesRepository favoritesRepository;

    @Test
    void success() {
        //given
        var user = new UserFixture().build();
        var favorites = new FavoritesFixture().build();
        given(favoritesRepository.findById(favorites.id()))
            .willReturn(Optional.of(favorites));
        given(favoritesRepository.existsByUserId(user.id()))
            .willReturn(true);

        //when
        var throwable = catchThrowable(() -> service.delete(user.id(), favorites.id()));

        //then
        assertThat(throwable).isNull();
        verify(favoritesRepository, times(1)).delete(favorites);
    }

    @Test
    void Poll에_좋아요를_요청한_사용자가_아니면_에러(){
        //given
        var user = new UserFixture()
            .setId(1L)
            .build();
        var favorites = new FavoritesFixture()
            .setUserId(2L)
            .build();
        given(favoritesRepository.findById(favorites.id()))
            .willReturn(Optional.of(favorites));
        given(favoritesRepository.existsByUserId(user.id()))
            .willReturn(false);

        //when
        var throwable = catchThrowable(() -> service.delete(user.id(), favorites.id()));

        //then
        assertThat(throwable).isNotNull();
    }
}