package org.tg.gollaba.common.web;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {
    private List<T> items;
    private Integer page;
    private Integer size;
    private Long totalCount;
    private Integer totalPage;

    public PageResponse(List<T> items,
                        Integer page,
                        Integer size,
                        Long totalCount) {
        this(items,
            page,
            size,
            totalCount,
            (int) Math.ceil((double) totalCount / size)
        );
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    public static <T> PageResponse<T> single(T value) {
        return new PageResponse<>(List.of(value), 0, 1, 1L);
    }

    public static <T> PageResponse<T> empty(Class<T> clazz) {
        return new PageResponse<>(Collections.emptyList(), 0, 1, 0L);
    }

    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }
}
