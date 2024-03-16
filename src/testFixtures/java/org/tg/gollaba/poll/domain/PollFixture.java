package org.tg.gollaba.poll.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.domain.Poll.PollResponseType;
import org.tg.gollaba.poll.domain.Poll.PollType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PollFixture implements TestFixture<Poll> {
    private Long id = 1L;
    private Long userId = 1L;
    private String title = "Sample Poll";
    private String creatorName = "John Doe";
    private PollResponseType responseType = PollResponseType.MULTIPLE;
    private PollType pollType = PollType.ANONYMOUS;
    private LocalDateTime endAt = LocalDateTime.now().plusWeeks(1).with(LocalTime.MAX);
    private Integer readCount = 0;
    private List<PollItem> items = new ArrayList<>(){{
        add(new PollItemFixture().setDescription("Option A").build());
        add(new PollItemFixture().setDescription("Option B").build());
    }};

    @Override
    public Poll build() {
        var poll = new Poll();
        FixtureReflectionUtils.reflect(poll, this);
        return poll;
    }
}
