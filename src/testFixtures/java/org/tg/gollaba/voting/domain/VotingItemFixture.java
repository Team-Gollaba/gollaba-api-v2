package org.tg.gollaba.voting.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class VotingItemFixture implements TestFixture<VotingItem> {
    private Long id = 1L;
    private Long pollItemId = 1L;

    @Override
    public VotingItem build() {
        var votingItem = new VotingItem();
        FixtureReflectionUtils.reflect(votingItem, this);
        return votingItem;
    }
}
