package org.tg.gollaba.stats.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;
import org.tg.gollaba.poll.domain.Poll;

import java.time.LocalDate;

@Entity
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollDailyStats extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollId;

    @Column(nullable = false)
    private LocalDate aggregationDate;

    @Column(nullable = false)
    private Integer voteCount;

    @Column(nullable = false)
    private Integer readCount;

    @Column(nullable = false)
    private Integer favoritesCount;
}