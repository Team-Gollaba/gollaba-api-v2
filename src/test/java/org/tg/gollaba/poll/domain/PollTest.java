package org.tg.gollaba.poll.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PollTest {

    @DisplayName("endAt이 null이면 기본값은 현재시간 + 일주일이다.")
    @Test
    void setEndDateTest() {
        //given
        var expected = LocalDateTime.now()
            .plusWeeks(1)
            .with(LocalTime.MAX);

        //when
        var result = new Poll(
            1L,
            "title",
            "creatorName",
            Poll.PollResponseType.MULTIPLE,
            Poll.PollType.ANONYMOUS,
            null,
            List.of(
                new PollItem("description1"),
                new PollItem("description2")
            )
        );

        //then
        assertThat(result.endAt()).isEqualTo(expected);
    }
    
    @DisplayName("투표 항목이 2개 미만이면 예외를 뱉는다.")
    @Test
    void whenItemSizeLessThanTwo_thenException() {
        //given
        var pollItems = List.of(
            new PollItem("description1")
        );

        //when
        var throwable = catchThrowable(() -> new Poll(
            1L,
            "title",
            "creatorName",
            Poll.PollResponseType.MULTIPLE,
            Poll.PollType.ANONYMOUS,
            null,
            pollItems
        ));

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("조회수를 올린다.")
    @Test
    void whenIncreaseReadCount(){
        //given
        var poll = new PollFixture()
            .setReadCount(0)
            .build();

        //when
        poll.increaseReadCount();

        //then
        assertThat(poll.readCount()).isEqualTo(1);
    }
}