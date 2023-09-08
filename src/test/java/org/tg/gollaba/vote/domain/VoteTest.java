package org.tg.gollaba.vote.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.vote.domain.Vote.RequiredVoterNameException;
;import java.util.Set;

class VoteTest {
    private VoteValidator voteValidator;

    @BeforeEach
    void setUp() {
        this.voteValidator = vote -> {
            // do nothing
        };
    }

    @DisplayName("투표자 생성시 익명 투표인 경우 이름이 '익명'으로 시작한다.")
    @Test
    void whenPollTypeAnonymous_thenVoterNameStartsWithAnonymous() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

        // when
        var vote = new Vote(
            poll,
            1L,
            null,
            Set.of(
                new VoteItem(poll.items().get(0).id())
            ),
            voteValidator
        );

        //then
        Assertions.assertThat(vote.voterName()).startsWith("익명");
    }

    @DisplayName("기명 투표일때 voterName이 비어 있으면 에러를 뱉는다.")
    @Test
    void whenPollTypeNamedAndVoterNameIsEmpty_thenThrowException() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.NAMED)
            .build();

        // when
        var throwable = Assertions.catchThrowable(() -> new Vote(
            poll,
            1L,
            null,
            Set.of(
                new VoteItem(poll.items().get(0).id())
            ),
            voteValidator
        ));

        //then
        Assertions.assertThat(throwable).isInstanceOf(RequiredVoterNameException.class);
    }
}