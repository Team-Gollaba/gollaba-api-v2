package org.tg.gollaba.vote.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.Poll.PollType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class Vote extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pollId;

    @Column
    private Long userId;

    @Column(nullable = false)
    private String voterName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "vote_id", nullable = false)
    private Set<VoteItem> items = new HashSet<>();

    public static final String ANONYMOUS_NAME_PREFIX = "익명-";

    public Vote(Poll poll,
                Long userId,
                String voterName,
                Set<VoteItem> items,
                VoteValidator validator) {
        this.pollId = poll.id();
        this.userId = userId;
        setVoterName(poll, voterName);
        this.items = items;
        validator.validate(this);
    }

    private void setVoterName(Poll poll, String voterName) {
        var pollType = poll.pollType();

        if (StringUtils.isBlank(voterName) && pollType == PollType.NAMED) {
            throw new RequiredVoterNameException();
        }

        if (pollType == PollType.ANONYMOUS) {
            this.voterName = ANONYMOUS_NAME_PREFIX + RandomStringUtils.randomAlphanumeric(7);
        } else {
            this.voterName = voterName;
        }
    }

    static class RequiredVoterNameException extends IllegalArgumentException {
        public RequiredVoterNameException() {
            super("기명 투표에는 투표자 이름이 필요합니다.");
        }
    }
}
