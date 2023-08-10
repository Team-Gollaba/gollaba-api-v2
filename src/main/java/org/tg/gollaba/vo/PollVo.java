package org.tg.gollaba.vo;

import org.tg.gollaba.domain.Poll;

public record PollVo(
  Long id,
  String name
){
    public static PollVo from(Poll poll) {
        return null; // TODO: 구현하기
    }
}
