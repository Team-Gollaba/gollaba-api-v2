package org.tg.gollaba.participation.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.participation.application.ParticipationValidator.InvalidParticipationException;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.participation.domain.ParticipationFixture;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ParticipationValidatorTest {
    @InjectMocks
    private ParticipationValidator validator;
    @Mock
    private PollRepository pollRepository;

    @DisplayName("투표가 종료되었으면 예외를 뱉는다.")
    @Test
    void checkPollEndTime() {
        //given
        var poll = new PollFixture()
            .setEndedAt(LocalDateTime.now().minusDays(1))
            .build();
        var participation = new ParticipationFixture().build();
        given(pollRepository.findById(eq(participation.poll().id())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(participation)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidParticipationException.class)
            .hasMessage(Status.POLL_IS_CLOSED.message());
    }

    @DisplayName("익명 투표일 경우 투표자 이름은 지정한 Prefix로 시작한다")
    @Test
    void validateVoteName() {
        //given
        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();
        var participation = new ParticipationFixture()
            .setParticipantName("test")
            .build();
        given(pollRepository.findById(eq(participation.poll().id())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(participation)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidParticipationException.class)
            .hasMessage(Status.ANONYMOUS_NAME_REQUIRED.message());
    }
}