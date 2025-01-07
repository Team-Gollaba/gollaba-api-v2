package org.tg.gollaba.voting.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class VotingFixture implements TestFixture<Voting> {
    private Long id = 1L;
    private Long pollId = 1L;
    private Long userId = 1L;
    private VoterName voterName = new VoterNameFixture().build();
    private Set<VotingItem> items = new HashSet<>(){{
        add(new VotingItemFixture().setId(1L).build());
    }};

    public VotingFixture addItem(VotingItem item) {
        items.add(item);
        return this;
    }

    @Override
    public Voting build() {
        var voting = new Voting();
        FixtureReflectionUtils.reflect(voting, this);
        return voting;
    }
}
