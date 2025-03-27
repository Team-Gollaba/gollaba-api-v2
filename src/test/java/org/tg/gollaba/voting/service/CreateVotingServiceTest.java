package org.tg.gollaba.voting.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.component.VotingValidator;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVotingServiceTest {
    @InjectMocks
    private VoteService service;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private VotingRepository votingRepository;
    @Mock
    private DuplicatedVotingChecker duplicatedVotingChecker;
    @Mock
    private VotingValidator votingValidator;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @DisplayName("투표 요청을 처리한다.")
    @Test
    void success() {
        // given
        var requirement = new RequirementFixture().build();
        var poll = new PollFixture().build();
        given(pollRepository.findById(eq(requirement.pollId())))
            .willReturn(Optional.of(poll));

        // when
        var throwable = Assertions.catchThrowable(() -> service.vote(requirement));

        // then
        assertThat(throwable).isNull();
        verify(duplicatedVotingChecker, times(1)).check(eq(requirement.ipAddress()), eq(requirement.pollId()));
        verify(duplicatedVotingChecker, times(1)).record(eq(requirement.ipAddress()), eq(requirement.pollId()));
        verify(votingRepository, times(1)).save(any());
    }

    @DisplayName("이미 진행된 투표면 에러가 발생한다.")
    @Test
    void alreadyVoted() {
        // given
        var requirement = new RequirementFixture().build();
        doThrow(new BadRequestException(Status.ALREADY_VOTING))
            .when(duplicatedVotingChecker).check(eq(requirement.ipAddress()), eq(requirement.pollId()));

        // when
        var throwable = Assertions.catchThrowable(() -> service.vote(requirement));

        // then
        assertThat(throwable).isInstanceOf(BadRequestException.class)
            .hasMessage(Status.ALREADY_VOTING.message());
        verify(duplicatedVotingChecker, times(1)).check(eq(requirement.ipAddress()), eq(requirement.pollId()));
    }

    @DisplayName("회원인 경우 투표가 중복인지 영속화된 데이터를 통해 확인한다.")
    @Test
    void alreadyVotedByUser() {
        // given
        var userId = 1L;
        var requirement = new RequirementFixture().userId(Optional.of(1L)).build();
        given(votingRepository.existsActiveVotingBy(eq(requirement.pollId()), eq(userId)))
            .willReturn(true);

        // when
        var throwable = Assertions.catchThrowable(() -> service.vote(requirement));

        // then
        assertThat(throwable).isInstanceOf(BadRequestException.class)
            .hasMessage(Status.ALREADY_VOTING.message());
        verify(duplicatedVotingChecker, times(1)).check(eq(requirement.ipAddress()), eq(requirement.pollId()));
        verify(votingRepository, times(1)).existsActiveVotingBy(eq(requirement.pollId()), eq(userId));
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static class RequirementFixture implements TestFixture<VoteService.Requirement> {
        private Long pollId = 1L;
        private Set<Long> pollItemIds = Set.of(1L);
        private String ipAddress = "0.0.0.0";
        private Optional<Long> userId = Optional.empty();
        private Optional<String> voterName = Optional.of("voterName");

        @Override
        public VoteService.Requirement build() {
            return new VoteService.Requirement(
                pollId,
                pollItemIds,
                ipAddress,
                userId,
                voterName
            );
        }
    }
}