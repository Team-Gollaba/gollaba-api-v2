package org.tg.gollaba.voting.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.domain.VotingFixture;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelVotingServiceTest {
    @InjectMocks
    private CancelVotingService service;
    @Mock
    private VotingRepository votingRepository;
    @Mock
    private DuplicatedVotingChecker duplicatedVotingChecker;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @DisplayName("회원이 투표를 철회한다.")
    @Test
    void success(){
        //given
        var voting = new VotingFixture().build();
        var ipAddress = "0.0.0.0";
        given(
            votingRepository.findById(voting.id()))
            .willReturn(Optional.of(voting)
        );

        //when
        service.cancel(voting.id(), ipAddress);

        //then
        assertNotNull(voting.deletedAt());
        verify(duplicatedVotingChecker, times(1)).delete(ipAddress, voting.pollId());
    }
}