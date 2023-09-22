package org.tg.gollaba.common.domain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tg.gollaba.domain.Poll;

import org.tg.gollaba.poll.PollFixture;
import org.tg.gollaba.poll.PollItemFixture;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PollTest {
    @DisplayName("전체 생성자 테스트")
    @Test
    void whenCreatePollFieldTest() {
        //when
        var poll = new Poll(
            1L,
            "투표이름",
            "아뉴나",
            Poll.PollType.ANONYMOUS,
            Poll.PollResponseType.MULTI,
            setEndedAt()
        );

        //then
        Assertions.assertThat(poll.getUserId()).isEqualTo(1L);

        Assertions.assertThat(poll.getPollType()).isEqualTo(Poll.PollType.ANONYMOUS);

        Assertions.assertThat(poll.getResponseType())
            .isIn(Poll.PollResponseType.MULTI, Poll.PollResponseType.SINGLE);

        Assertions.assertThat(poll.getReadCount()).isEqualTo(0);

        Assertions.assertThat(poll.getEndedAt()).isEqualTo(setEndedAt());
    }
    private LocalDateTime setEndedAt(){ //초 단위까지 맞출 자신이 없어서 강제로 값 주입 ...
        var dateString = "2023-09-27 18:05:36";
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }


    @DisplayName("투표 생성시 사용자가 마감기한을 입력하지 않았다면, 마감기한은 일주일 후이다.")
    @Test
    void whenCreatePollEndedAtFieldTest() {
        //when
        var poll = new Poll(
            1L,
            "투표이름",
            "아뉴나",
            Poll.PollType.ANONYMOUS,
            Poll.PollResponseType.MULTI,
            null
        );

        //then > 엄청 작은 초 차이를 이기지 못하고 1초 차이 이내인지 검사로 변경
        var duration = Duration.between(poll.getEndedAt(), LocalDateTime.now().plusDays(7));
        var secondsDifference = Math.abs(duration.getSeconds());

        Assertions.assertThat(secondsDifference)
            .isLessThanOrEqualTo(1);
    }

    @DisplayName("투표 생성시 사용자가 투표 항목을 1개만 입력했다면 예외가 터진다")
    @Test
    void whenCreatePollItemRangeTest() {
        //given
        var poll = new PollFixture().build();
        var item = List.of(
            new PollItemFixture().setId(1L).build()
        );

        //when, then
        Assertions.assertThatThrownBy(() -> poll.updatePollItems(item)) //예외를 발생시키는 코드
            .isInstanceOf(Poll.InvalidOptionSizeException.class) //예외내용
            .hasMessage("지정할 수 있는 항목의 범위가 아닙니다.");
        //ㄴ> updatePollOptions를 할 때 예외가 발생하기 때문에 when에서 이미 예외가 터짐 ... 따라서 위와 같이 테스트 수정
    }

    @DisplayName("투표 생성시 사용자가 투표 항목을 11개를 입력했다면 예외가 터진다")
    @Test
    void whenCreatePollOptionOverTest() {
        //given
        var poll = new PollFixture().build();
        var items = IntStream.rangeClosed(1, 11)
            .mapToObj(i -> new PollItemFixture().setId((long) i).build())
            .collect(Collectors.toList());

        //when, then
        Assertions.assertThatThrownBy(() -> poll.updatePollItems(items))
            .isInstanceOf(Poll.InvalidOptionSizeException.class) //예외내용
            .hasMessage("지정할 수 있는 항목의 범위가 아닙니다.");
    }
}
