package org.tg.gollaba.common.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CachedList<T> {
    private List<T> list;

    public static <T> CachedList<T> from(List<T> list) {
        return new CachedList<>(list);
    }
}
