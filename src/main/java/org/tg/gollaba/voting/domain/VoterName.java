package org.tg.gollaba.voting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.poll.domain.Poll;

@Embeddable
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoterName {
    public static final String ANONYMOUS_NAME_PREFIX = "익명-";

    @Column(name = "voter_name", nullable = false)
    private String value;

    public VoterName(Poll poll,
                     String value) {
        setVoterName(poll, value);
    }

    private void setVoterName(Poll poll, String value) {
        var pollType = poll.pollType();

        if (StringUtils.isBlank(value) && pollType == Poll.PollType.NAMED) {
            throw new RequiredVoterNameException();
        }

        if (pollType == Poll.PollType.ANONYMOUS) {
            this.value = ANONYMOUS_NAME_PREFIX + RandomStringUtils.randomAlphanumeric(7);
        } else {
            this.value = value;
        }
    }

    static class RequiredVoterNameException extends IllegalArgumentException {
        public RequiredVoterNameException() {
            super("기명 투표에는 투표자 이름이 필요합니다.");
        }
    }
}
