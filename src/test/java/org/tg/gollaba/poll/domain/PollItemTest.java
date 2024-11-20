package org.tg.gollaba.poll.domain;

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
        void 투표_항목_설명_변경(){
            //given
            var poll = new PollFixture().build();
            var pollItem = poll.items().get(0);

            //when
            pollItem.changeDescription("updateDescription");

            //then
            assertThat(pollItem.description()).isEqualTo("updateDescription");
        }
    }

}