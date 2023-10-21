package org.tg.gollaba.participation.domain;

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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class Participation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false, updatable = false)
    private Poll poll;

    @Column
    private Long userId;

    @Column(name = "voter_name", nullable = false)
    private String participantName;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "participation_id", nullable = false)
    private Set<ParticipationItem> items = new HashSet<>();

    public static final String ANONYMOUS_NAME_PREFIX = "익명-";

    public Participation(Poll poll,
                         Long userId,
                         String participantName,
                         Set<ParticipationItem> items) {
        this.poll = poll;
        this.userId = userId;
        setParticipantName(poll, participantName);
        this.items = items;
    }

    private void setParticipantName(Poll poll, String voterName) {
        var pollType = poll.pollType();

        if (StringUtils.isBlank(voterName) && pollType == PollType.NAMED) {
            throw new RequiredVoterNameException();
        }

        if (pollType == PollType.ANONYMOUS) {
            this.participantName = ANONYMOUS_NAME_PREFIX + RandomStringUtils.randomAlphanumeric(7);
        } else {
            this.participantName = voterName;
        }
    }

    static class RequiredVoterNameException extends IllegalArgumentException {
        public RequiredVoterNameException() {
            super("기명 투표에는 투표자 이름이 필요합니다.");
        }
    }

    public void cancel(){
        if (deletedAt != null){
            throw new AlreadyCancelException();
        }

        this.deletedAt = LocalDateTime.now();
    }

    static class AlreadyCancelException extends IllegalArgumentException{
        public AlreadyCancelException(){
            super("이미 투표가 철회됐습니다.");
        }
    }
}
