package org.tg.gollaba.vote.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.domain.PollOptionFixture;
import org.tg.gollaba.vote.domain.Voter.RequiredVoterNameException;
;

class VoterTest {

    @DisplayName("투표자 생성시 익명 투표인 경우 이름이 '익명'으로 시작한다.")
    @Test
    void whenPollTypeAnonymous_thenVoterNameStartsWithAnonymous() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();
        var pollItem = new PollOptionFixture()
            .setPoll(poll)
            .build();

        // when
        var voter = new Voter(
                pollItem.getPoll().getId(),
            1L,
            null,
                pollItem
            );

        //then
        Assertions.assertThat(voter.voterName()).startsWith("익명");
    }

    @DisplayName("기명 투표일때 voterName이 비어 있으면 에러를 뱉는다.")
    @Test
    void whenPollTypeNamedAndVoterNameIsEmpty_thenThrowException() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.NAMED)
            .build();
        var pollItem = new PollOptionFixture()
            .setPoll(poll)
            .build();

        // when
        var throwable = Assertions.catchThrowable(() -> new Voter(
            pollItem.getPoll().getId(),
            1L,
            null,
            pollItem
        ));

        //then
        Assertions.assertThat(throwable).isInstanceOf(RequiredVoterNameException.class);
    }
}