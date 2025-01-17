package org.tg.gollaba.voting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.voting.domain.VoterName.RequiredVoterNameException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class VoterNameTest {

    @DisplayName("투표자 이름 생성시 익명 투표인 경우 이름이 '익명'으로 시작한다.")
    @Test
    void whenPollTypeAnonymous_thenVoterNameStartsWithAnonymous() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

        // when
        var voterName = new VoterName(
            poll,
            null
        );

        //then
        assertThat(voterName.value()).startsWith("익명");
    }

    @DisplayName("기명 투표일때 voterName이 비어 있으면 에러를 뱉는다.")
    @Test
    void whenPollTypeNamedAndVoterNameIsEmpty_thenThrowException() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.NAMED)
            .build();

        // when
        var throwable = catchThrowable(() -> new VoterName(poll, null));

        //then
        assertThat(throwable).isInstanceOf(RequiredVoterNameException.class);
    }

    @DisplayName("기명 투표시 voterName에 #으로 시작하는 랜덤 해시태그가 붙는다")
    @Test
    void shouldAddHashTagToVoterNameWhenPollTypeIsNamed() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.NAMED)
            .build();

        // when
        var voterName = new VoterName(poll, "testName");

        //then
        assertThat(voterName.value())
            .startsWith("testName #")
            .matches("testName #\\d{5}");
    }
}