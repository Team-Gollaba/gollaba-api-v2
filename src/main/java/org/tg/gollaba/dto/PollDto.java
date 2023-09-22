package org.tg.gollaba.dto;

import org.tg.gollaba.domain.Poll;

import java.time.LocalDateTime;

public record PollDto(
                       Long id,
                       Long userId,
                       String title,
                       String creatorName,
                       Poll.PollResponseType responseType,
                       Poll.PollType pollType,
                       LocalDateTime endedAt,
                       Integer readCount
){}
