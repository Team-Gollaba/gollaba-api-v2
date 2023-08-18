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
        var pollFixture = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();
        var pollItemFixture = new PollOptionFixture()
            .setPoll(pollFixture)
            .build();

        //when
        var poll = new Poll(
            pollFixture.getUserId(),
            pollFixture.getTitle(),
            pollFixture.getCreatorName(),
            pollFixture.getPollType(),
            pollFixture.getResponseType()
        );


        var pollOption = new PollOption(
            pollItemFixture.getPoll(),
            pollItemFixture.getDescription(),
            pollItemFixture.getImageUrl()
        );

        //then
        Assertions.assertThat(poll.getUserId())
            .isInstanceOf(Long.class).isEqualTo(1L);

        Assertions.assertThat(poll.getPollType())
            .isIn(Poll.PollType.NAMED, Poll.PollType.ANONYMOUS);

        Assertions.assertThat(poll.getResponseType())
            .isIn(Poll.PollResponseType.MULTI, Poll.PollResponseType.SINGLE);

        Assertions.assertThat(poll.getReadCount())
            .isEqualTo(0);

        Assertions.assertThat(pollOption.getDescription())
            .isNotNull();

        Assertions.assertThat(pollOption.getImageUrl())
            .isInstanceOf(String.class).startsWith("https://example.com/imageA.jpg");
    }


    @DisplayName("투표 생성시 마감기간은 투표 생성일 기준으로 일주일 후이다.")
    @Test
    void whenCreatePollEndedAtFieldTest() {
        //given
        var pollFixture = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

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
        var pollFixture = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

        var poll = new Poll(
            pollFixture.getUserId(),
            pollFixture.getTitle(),
            pollFixture.getCreatorName(),
            pollFixture.getPollType(),
            pollFixture.getResponseType()
        );

        List<PollOption> pollOptionFixtures = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            pollOptionFixtures.add(
                new PollOptionFixture()
                    .setPoll(pollFixture)
                    .build());
        }

        List<PollOption> pollOptions = new ArrayList<>();
        for (PollOption pollOptionFixture : pollOptionFixtures) {
            PollOption pollOption = new PollOption(
                pollOptionFixture.getPoll(),
                pollOptionFixture.getDescription(),
                pollOptionFixture.getImageUrl()
            );
            pollOptions.add(pollOption);
        }

        //when
        poll.addPollOptions(pollOptions);

        //then
        var optionSize = poll.getOptions().size();

        Assertions.assertThat(optionSize)
            .as("항목은 최소 2개부터 최대 10개까지 입니다")
            .isBetween(2, 10);
    }
}
