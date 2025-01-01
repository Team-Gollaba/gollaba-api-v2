package org.tg.gollaba.stats.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollSearchStats extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchedWord;

    public PollSearchStats(String searchedWord){
        this.searchedWord = searchedWord;
    }
}
