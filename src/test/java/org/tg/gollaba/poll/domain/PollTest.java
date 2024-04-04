package org.tg.gollaba.poll.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PollTest {

    @DisplayName("endAt이 null이면 기본값은 현재시간 + 일주일이다.")
    @Test
    void setEndDateTest() {
        //given
        var expected = now()
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

    @DisplayName("Poll 정보를 업데이트한다")
    @Test //도메인 테스트라 test객체 X
    void update(){
        //given
        var poll = new PollFixture().build();
        var changePollItems = List.of(
            new PollItem(
                "changeDescription"
            )
        );

        //when
        poll.update(
            "changeTitle",
            LocalDateTime.now().plusMinutes(60),
            changePollItems
        );

        //then
        //endAt이 null이 아님
        //poll의 endAt보다 현재 시간이 30분이 넘음
        //title이 null이 아님
        //title의 값이 changeTitle이 됨
        //items의 description이 변경됨
        assertNotNull(poll.endAt());
        assertTrue(now().plusMinutes(30).isAfter(poll.endAt())); //TODO 여기서 에러 얘는 또 머야 ㅠㅠㅠㅠㅠ
        assertNotNull(poll.title());
        assertEquals("changeTitle", poll.title());
        assertEquals("changeDescription", poll.items().get(0).description());
    }
}