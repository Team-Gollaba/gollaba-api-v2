package org.tg.gollaba.common.domain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tg.gollaba.domain.Poll;

import org.tg.gollaba.poll.PollFixture;
import org.tg.gollaba.poll.PollItemFixture;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


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

        //1초 차이 이내인지
        var duration = Duration.between(poll.getEndedAt(), LocalDateTime.now().plusDays(7));
        var secondsDifference = Math.abs(duration.getSeconds());

        Assertions.assertThat(secondsDifference)
            .isLessThanOrEqualTo(1);
    }

    @DisplayName("투표 생성시 사용자가 투표 항목을 1개 또는 11개 입력했다면 예외가 터진다")
    @ParameterizedTest
    @ValueSource(ints = {1, 11}) //대입하려는 숫자
    void whenCreatePollItemRangeTest(int pollItem) {
        //given
        var poll = new PollFixture().build();
        var items = IntStream.rangeClosed(1, pollItem)
            .mapToObj(i -> new PollItemFixture().setId((long) i).build())
            .collect(Collectors.toList());

        //when, then
        assertThatThrownBy(() -> poll.updatePollItems(items))
            .isInstanceOf(Poll.InvalidOptionSizeException.class)
            .hasMessage("지정할 수 있는 항목의 범위가 아닙니다.");
    }
}
