package org.tg.gollaba.voting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

import java.util.Objects;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class VotingItem extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollItemId;

    public VotingItem(Long pollItemId) {
        this.pollItemId = pollItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotingItem that)) return false;

        return Objects.equals(
            pollItemId,
            that.pollItemId
        );
    }

    @Override
    public int hashCode() {
        return pollItemId != null ? pollItemId.hashCode() : 0;
    }
}
