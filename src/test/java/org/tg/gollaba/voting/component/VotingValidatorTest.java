package org.tg.gollaba.voting.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItemFixture;
import org.tg.gollaba.voting.component.VotingValidator.InvalidVotingException;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.voting.domain.VotingFixture;
import org.tg.gollaba.voting.domain.VotingItemFixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VotingValidatorTest {
    @InjectMocks
    private VotingValidator validator;
    @Mock
    private PollRepository pollRepository;

    @DisplayName("투표가 종료되었으면 예외를 뱉는다.")
    @Test
    void checkPollEndTime() {
        //given
        var poll = new PollFixture()
            .setEndedAt(LocalDateTime.now().minusDays(1))
            .build();
        var voting = new VotingFixture().build();
        given(pollRepository.findById(eq(voting.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(voting)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidVotingException.class)
            .hasMessage(Status.POLL_IS_CLOSED.message());
    }

    @DisplayName("투표 아이템이 없으면 예외를 뱉는다.")
    @Test
    void validateVotingItems_empty() {
        //given
        var poll = new PollFixture().build();
        var voting = new VotingFixture()
            .setItems(Set.of())
            .build();
        given(pollRepository.findById(eq(voting.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(voting)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidVotingException.class)
            .hasMessage(Status.INVALID_VOTING_ITEM_SIZE.message());
    }

    @DisplayName("단일 선택 투표에서 투표 아이템이 2개 이상이면 예외를 뱉는다.")
    @Test
    void validateVotingItems_single() {
        //given
        var poll = new PollFixture()
            .setResponseType(Poll.PollResponseType.SINGLE)
            .build();
        var voting = new VotingFixture()
            .setItems(Set.of(
                new VotingItemFixture().setPollItemId(1L).build(),
                new VotingItemFixture().setPollItemId(2L).build()
            ))
            .build();
        given(pollRepository.findById(eq(voting.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(voting)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidVotingException.class)
            .hasMessage(Status.INVALID_SINGLE_VOTING_ITEM_SIZE.message());
    }

    @DisplayName("올바르지 않은 투표 항목을 선택 시 예외를 뱉는다.")
    @Test
    void validateVotingItems_multiple() {
        //given
        var poll = new PollFixture()
            .setResponseType(Poll.PollResponseType.MULTIPLE)
            .setItems(List.of(
                new PollItemFixture().setId(1L).build(),
                new PollItemFixture().setId(2L).build()
            ))
            .build();
        var voting = new VotingFixture()
            .setItems(Set.of(
                new VotingItemFixture().setPollItemId(1L).build(),
                new VotingItemFixture().setPollItemId(2L).build(),
                new VotingItemFixture().setPollItemId(3L).build(),
                new VotingItemFixture().setPollItemId(4L).build()
            ))
            .build();
        given(pollRepository.findById(eq(voting.pollId())))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(
            () -> validator.validate(voting)
        );

        //then
        assertThat(throwable).isInstanceOf(InvalidVotingException.class)
            .hasMessage(Status.INVALID_VOTING_ITEM.message());
    }
}