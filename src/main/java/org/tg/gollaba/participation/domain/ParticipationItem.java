package org.tg.gollaba.participation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class ParticipationItem extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollItemId;

    public ParticipationItem(Long pollItemId) {
        this.pollItemId = pollItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipationItem participationItem)) return false;

        return pollItemId.equals(participationItem.pollItemId);
    }

    @Override
    public int hashCode() {
        return pollItemId.hashCode();
    }
}
