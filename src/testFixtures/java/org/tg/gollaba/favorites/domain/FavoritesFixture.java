package org.tg.gollaba.favorites.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class FavoritesFixture implements TestFixture<Favorites> {
    private Long id = 1L;
    private Long userId = 1L;
    private Long pollId = 1L;

    @Override
    public Favorites build() {
        var favorites = new Favorites();
        FixtureReflectionUtils.reflect(favorites, this);
        return favorites;
    }
}
