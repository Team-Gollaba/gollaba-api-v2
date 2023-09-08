package org.tg.gollaba.vote.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.poll.domain.PollFixture;

@Getter
@Setter
@Accessors(chain = true)
public class VoteItemFixture implements TestFixture<VoteItem> {
    private Long id = 1L;
    private Long pollItemId = 1L;

    @Override
    public VoteItem build() {
        var voteItem = new VoteItem();
        FixtureReflectionUtils.reflect(voteItem, this);
        return voteItem;
    }
}
