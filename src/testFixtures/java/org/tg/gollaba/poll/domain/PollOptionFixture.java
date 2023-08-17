package org.tg.gollaba.poll.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.vote.domain.Voter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class PollOptionFixture implements TestFixture<PollOption> {
    private Long id = 1L;
    private Poll poll = new PollFixture().build();
    private String description = "Option A";
    private String imageUrl = "https://example.com/imageA.jpg";
    private Set<Voter> voters = new HashSet<>();

    @Override
    public PollOption build() {
        var pollOption = new PollOption();
        FixtureReflectionUtils.reflect(pollOption, this);
        return pollOption;
    }
}
