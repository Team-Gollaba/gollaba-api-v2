package org.tg.gollaba.vo;

import jakarta.persistence.*;
import org.tg.gollaba.domain.Poll;

import java.time.LocalDateTime;

public record PollVo( //얘가 dto 일 거임
    Long id,
    Long userId,
    String title,
    String creatorName,
    Poll.PollResponseType responseType,
    Poll.PollType pollType,
    LocalDateTime endedAt,
    Integer readCount
){
    public static PollVo from(Poll poll) {
        return new PollVo(
                poll.getId(),
                poll.getUserId(),
                poll.getTitle(),
                poll.getCreatorName(),
                poll.getResponseType(),
                poll.getPollType(),
                poll.getEndedAt(),
                poll.getReadCount()
        );
    }
}
