package org.tg.gollaba.vote.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.Poll.PollType;
import org.tg.gollaba.domain.PollOption;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollId;


    @Column // TODO: User 연관관계 매핑
    private Long userId;

    @Column(nullable = false)
    private String voterName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    private static final String ANONYMOUS_NAME_PREFIX = "익명";


    public Voter(Long pollId,
                 Long userId,
                 String voterName,
                 PollOption pollOption) {
        this.pollId = pollId;
        this.userId = userId;
        this.pollOption = pollOption;
        setVoterName(voterName);
    }

    private void setVoterName(String voterName) {
        var pollType = pollOption.getPoll().getPollType();

        if (StringUtils.isBlank(voterName) && pollType == PollType.NAMED) {
            throw new RequiredVoterNameException();
        }

        if (pollType == PollType.ANONYMOUS) {
            this.voterName = ANONYMOUS_NAME_PREFIX + RandomStringUtils.random(7);
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
