package org.tg.gollaba.poll.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PollItemTest {

    @Nested
    class changeImageUrl {
        @Test
        void success() {
            // given
            var poll = new PollFixture().build();
            var pollItem = poll.items().get(0);

            // when
            pollItem.changeImageUrl("http://example.com/image.jpg");

            // then
            assertThat(pollItem.imageUrl()).isEqualTo("http://example.com/image.jpg");
        }

        @Test
        void 비정상적인_url_이면_실패() {
            // given
            var poll = new PollFixture().build();
            var pollItem = poll.items().get(0);

            // when
            var throwable = Assertions.catchThrowable(() -> pollItem.changeImageUrl("invalid url"));

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[PollItem][changeImageUrl] 유효하지 않은 URL 입니다.");
        }

        @Test
        void null이_아니라면_항목의_설명값을_변경한다(){
            //given
            var poll = new PollFixture().build();
            var pollItem = poll.items().get(0);
            String changeInfo = "test";

            //when
            pollItem.changeDescription(changeInfo);

            //then
            assertThat(pollItem.description()).isEqualTo(changeInfo);
        }
    }

}