package org.tg.gollaba.participation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class ParticipationItemFixture implements TestFixture<ParticipationItem> {
    private Long id = 1L;
    private Long pollItemId = 1L;

    @Override
    public ParticipationItem build() {
        var participationItem = new ParticipationItem();
        FixtureReflectionUtils.reflect(participationItem, this);
        return participationItem;
    }
}
