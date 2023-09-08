package org.tg.gollaba.vote.domain;

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
public class VoteFixture implements TestFixture<Vote> {
    private Long id = 1L;
    private Long userId = 1L;
    private Long pollId = 1L;
    private String voterName = "John Doe";
    private Set<VoteItem> items = new HashSet<>(){{
        add(new VoteItemFixture().setId(1L).build());
    }};

    public VoteFixture addItem(VoteItem item) {
        items.add(item);
        return this;
    }

    @Override
    public Vote build() {
        var voter = new Vote();
        FixtureReflectionUtils.reflect(voter, this);
        return voter;
    }
}
