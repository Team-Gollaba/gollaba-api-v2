package org.tg.gollaba.poll;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.Poll.PollResponseType;
import org.tg.gollaba.domain.Poll.PollType;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class PollFixture implements TestFixture<Poll>{
    private Long id = 1L;
    private Long userId = 1L;
    private String title = "Sample Poll";
    private String creatorName = "John Doe";
    private PollResponseType responseType = PollResponseType.SINGLE;
    private PollType pollType = PollType.ANONYMOUS;
    private LocalDateTime endedAt = LocalDateTime.now().plusDays(7);
    private Integer readCount = 0;

    @Override
    public Poll build() {
        var poll = new Poll(
            userId,
            title,
            creatorName,
            pollType,
            responseType,
            endedAt
        );
        FixtureReflectionUtils.reflect(poll, this);
        return poll;
    }
}
