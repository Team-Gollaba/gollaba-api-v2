package org.tg.gollaba.stats.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Immutable;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Immutable
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollStats extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollId;

    @Column(nullable = false)
    private Integer totalVoteCount;

    @Column(nullable = false)
    private Integer totalReadCount;

    @Column(nullable = false)
    private Integer totalFavoritesCount;
}
