package org.tg.gollaba.stats.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Table(name = "poll_search_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollSearchStat extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchedWord;

    @Column
    private Long userId;

    public PollSearchStat(String searchedWord,
                          Long userId){
        this.searchedWord = searchedWord;
        this.userId = userId;
    }
}
