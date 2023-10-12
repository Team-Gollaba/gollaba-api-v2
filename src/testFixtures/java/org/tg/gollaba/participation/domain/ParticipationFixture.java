package org.tg.gollaba.participation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class ParticipationFixture implements TestFixture<Participation> {
    private Long id = 1L;
    private Long userId = 1L;
    private Poll poll = new PollFixture().build();
    private String participantName = "John Doe";
    private Set<ParticipationItem> items = new HashSet<>(){{
        add(new ParticipationItemFixture().setId(1L).build());
    }};

    public ParticipationFixture addItem(ParticipationItem item) {
        items.add(item);
        return this;
    }

    @Override
    public Participation build() {
        var participation = new Participation();
        FixtureReflectionUtils.reflect(participation, this);
        return participation;
    }
}
