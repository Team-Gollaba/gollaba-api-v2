package org.tg.gollaba.poll.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.tg.gollaba.poll.controller.serializer.HashIdSerializer;
import org.tg.gollaba.poll.domain.Poll;

import java.time.LocalDateTime;
import java.util.List;

public record PollSummary(
    @JsonSerialize(using = HashIdSerializer.class)
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
