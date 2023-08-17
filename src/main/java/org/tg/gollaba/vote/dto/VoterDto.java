package org.tg.gollaba.vote.dto;

import org.tg.gollaba.domain.Poll;

public record VoterDto(
  Long id,
  String name
){
    public static VoterDto from(Poll poll) {
        return null; // TODO: 구현하기
    }
}
