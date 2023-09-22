package org.tg.gollaba.poll;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollItem;
//import org.tg.gollaba.vote.domain.Voter;


@Getter
@Setter
@Accessors(chain = true)
public class PollItemFixture implements TestFixture<PollItem>{
    private Long id = 1L;
    private Poll poll = new PollFixture().build();
    private String description = "Option A";
    private String imageUrl = "https://example.com/imageA.jpg";
//    private Set<Voter> voters = new HashSet<>();

    @Override
    public PollItem build() {
        var pollItem = new PollItem(
            poll,
            description,
            imageUrl
        );
        FixtureReflectionUtils.reflect(pollItem, this);
        return pollItem;
    }
}
