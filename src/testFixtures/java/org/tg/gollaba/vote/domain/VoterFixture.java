package org.tg.gollaba.vote.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.poll.domain.PollOptionFixture;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class VoterFixture implements TestFixture<Voter> {
    private Long id = 1L;
    private Long userId = 1L;
    private String voterName = "John Doe";
    private PollOption pollOption;

    @Override
    public Voter build() {
        var voter = new Voter();
        var pollOption = new PollOptionFixture()
            .setVoters(Set.of(voter))
            .build();
        this.setPollOption(pollOption);
        FixtureReflectionUtils.reflect(voter, this);

        return voter;
    }
}
