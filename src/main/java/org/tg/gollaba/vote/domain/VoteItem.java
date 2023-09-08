package org.tg.gollaba.vote.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.domain.Poll.PollType;
import org.tg.gollaba.domain.PollOption;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class VoteItem extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollItemId;

    public VoteItem(Long pollItemId) {
        this.pollItemId = pollItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteItem voteItem)) return false;

        return pollItemId.equals(voteItem.pollItemId);
    }

    @Override
    public int hashCode() {
        return pollItemId.hashCode();
    }
}
