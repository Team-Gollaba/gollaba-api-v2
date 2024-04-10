package org.tg.gollaba.voting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CheckVotingServiceTest {
    @InjectMocks
    private CheckVotingService checkVotingService;
    @Mock
    private VotingRepository votingRepository;
    @Mock
    private DuplicatedVotingChecker duplicatedVotingChecker;

    @Test
    void 투표를_아직_안한_경우_false() {
        // given
        var requirement = new CheckVotingService.Requirement(
            1L,
            "0.0.0.0",
            Optional.of(2L)
        );
        given(votingRepository.existsActiveVotingBy(requirement.pollId(), requirement.userId().get()))
            .willReturn(false);

        // when
        var result = checkVotingService.check(requirement);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 투표를_이미_했을_경우_true_케이스_익명() {
        // given
        var requirement = new CheckVotingService.Requirement(
            1L,
            "0.0.0.0",
            Optional.empty()
        );
        doThrow(new BadRequestException(Status.ALREADY_VOTING))
            .when(duplicatedVotingChecker).check(requirement.ipAddress(), requirement.pollId());

        // when
        var result = checkVotingService.check(requirement);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 투표를_이미_했을_경우_true_케이스_회원() {
        // given
        var requirement = new CheckVotingService.Requirement(
            1L,
            "0.0.0.0",
            Optional.of(2L)
        );
        given(votingRepository.existsActiveVotingBy(requirement.pollId(), requirement.userId().get()))
            .willReturn(true);

        // when
        var result = checkVotingService.check(requirement);

        // then
        assertThat(result).isTrue();
    }
}