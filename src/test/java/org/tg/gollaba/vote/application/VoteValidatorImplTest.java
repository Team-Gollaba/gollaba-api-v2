package org.tg.gollaba.vote.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.vote.application.VoteValidatorImpl.VoteValidationException;
import org.tg.gollaba.vote.domain.Vote;
import org.tg.gollaba.vote.domain.VoteFixture;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VoteValidatorImplTest {
    @InjectMocks
    private VoteValidatorImpl voteValidator;

    @Mock
    private PollRepository pollRepository;

    @DisplayName("투표가 종료되었으면 예외를 뱉는다.")
    @Test
    void checkPollEndTime() {
        //given
        var poll = new PollFixture()
            .setEndedAt(LocalDateTime.now().minusDays(1))
            .build();
        var vote = new VoteFixture().build();
        given(pollRepository.findById(eq(vote.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = Assertions.catchThrowable(
            () -> voteValidator.validate(vote)
        );

        //then
        assertThat(throwable).isInstanceOf(VoteValidationException.class)
            .hasMessage(Status.POLL_IS_CLOSED.message());
    }

    @DisplayName("익명 투표일 경우 투표자 이름은 지정한 Prefix로 시작한다")
    @Test
    void validateVoteName() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();
        var vote = new VoteFixture()
            .setVoterName("test")
            .build();
        given(pollRepository.findById(eq(vote.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = Assertions.catchThrowable(
            () -> voteValidator.validate(vote)
        );

        //then
        assertThat(throwable).isInstanceOf(VoteValidationException.class)
            .hasMessage(Status.ANONYMOUS_NAME_REQUIRED.message());
    }
}