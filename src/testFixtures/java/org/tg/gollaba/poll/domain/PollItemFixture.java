package org.tg.gollaba.poll.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class PollItemFixture implements TestFixture<PollItem> {
    private Long id = 1L;
    private String description = "Option A";
    private String imageUrl = "https://example.com/imageA.jpg";

    @Override
    public PollItem build() {
        var pollItem = new PollItem();
        FixtureReflectionUtils.reflect(pollItem, this);
        return pollItem;
    }
}
