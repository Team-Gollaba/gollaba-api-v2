package org.tg.gollaba.participation.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.participation.domain.Participation.RequiredVoterNameException;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;

import java.util.Set;

class ParticipationTest {
    @DisplayName("투표자 생성시 익명 투표인 경우 이름이 '익명'으로 시작한다.")
    @Test
    void whenPollTypeAnonymous_thenVoterNameStartsWithAnonymous() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

        // when
        var vote = new Participation(
            poll,
            1L,
            null,
            Set.of(
                new ParticipationItem(poll.items().get(0).id())
            )
        );

        //then
        Assertions.assertThat(vote.participantName()).startsWith("익명");
    }

    @DisplayName("기명 투표일때 voterName이 비어 있으면 에러를 뱉는다.")
    @Test
    void whenPollTypeNamedAndVoterNameIsEmpty_thenThrowException() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.NAMED)
            .build();

        // when
        var throwable = Assertions.catchThrowable(() -> new Participation(
            poll,
            1L,
            null,
            Set.of(
                new ParticipationItem(poll.items().get(0).id())
            )
        ));

        //then
        Assertions.assertThat(throwable).isInstanceOf(RequiredVoterNameException.class);
    }
}