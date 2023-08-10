package org.tg.gollaba.dto;

import org.tg.gollaba.domain.Poll;

import java.time.LocalDateTime;

public record PollDto( //얘가 dto 일 거임
                       Long id,
                       Long userId,
                       String title,
                       String creatorName,
                       Poll.PollResponseType responseType,
                       Poll.PollType pollType,
                       LocalDateTime endedAt,
                       Integer readCount
){
    public static PollDto from(Poll poll) {
        return new PollDto(
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
