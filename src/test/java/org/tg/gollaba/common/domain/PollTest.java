package org.tg.gollaba.common.domain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.domain.Poll;

import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.poll.PollFixture;
import org.tg.gollaba.poll.PollOptionFixture;

import java.util.ArrayList;
import java.util.List;

public class PollTest {
    @DisplayName("전체 생성자 테스트")
    @Test
    void whenCreatePollFieldTest() {
        //given

        //when
        var poll = new Poll(
            1L,
            ~~~,
            ~~,
            ~~,
            ~~
        );

        //then
        Assertions.assertThat(poll.getUserId()).isEqualTo(1L);
        Assertions.assertThat(poll.getPollType()).isEqualTo(Poll.PollType.ANONYMOUS);
        ....
        
    }


    @DisplayName("투표 생성시 마감기간은 투표 생성일 기준으로 일주일 후이다.")
    @Test
    void whenCreatePollEndedAtFieldTest() {
        //given

        //when
        var poll = new Poll(
            pollFixture.getUserId(),
            pollFixture.getTitle(),
            pollFixture.getCreatorName(),
            pollFixture.getPollType(),
            pollFixture.getResponseType()
        );

        //then
        Assertions.assertThat(poll.getEndedAt()
                .isEqual(poll.createdAt().plusDays(7)))
                .isTrue();
    }

    @DisplayName("투표 생성시 투표 항목은 2개 부터 10개까지이다")
    @Test
    void whenCreatePollOptionRangeTest() {
        //given
        var poll = new PollFixture().build();
        var options = List.of(
            new PollOptionFixture().setId(1L).build()
        );

        var overSizeOptions = new ArrayList<>();

        for (int i = 0 <11) {
            overSizeOptions.add(
                new PollOptionFixture().setId(i + 1).build()
            )
        }
        //when
        var throwable1 = poll.addPollOptions(options);
        var throwable2 = catchThrowable(() -> poll addPollOptions(overSizeOptions));

        //then
        assertThat(throwable1).isIstanceOf()
        assertThat(throwable2).isIstanceOf(InvalidOptionSizeException.class);
    }
}
