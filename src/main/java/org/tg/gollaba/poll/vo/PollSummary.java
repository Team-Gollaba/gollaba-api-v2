package org.tg.gollaba.poll.vo;

import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.time.LocalDateTime;
import java.util.List;

public record PollSummary(
    Long id,
    String title,
    String creatorName,
    Poll.PollResponseType responseType,
    Poll.PollType pollType,
    LocalDateTime endAt,
    Integer readCount,
    Integer totalVotingCount,
    List<PollItem> items
) {
    public record PollItem(
        Long id,
        String description,
        String imageUrl,
        Integer votingCount
    ) {
    }
}
