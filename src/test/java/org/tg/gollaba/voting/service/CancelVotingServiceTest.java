package org.tg.gollaba.voting.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.voting.domain.VotingFixture;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CancelVotingServiceTest {
    @InjectMocks
    private CancelVotingService service;

    @Mock
    private VotingRepository votingRepository;

    @DisplayName("회원이 투표를 철회한다.")
    @Test
    void success(){
        //given
        var voting = new VotingFixture().build();

        given(
            votingRepository.findById(voting.id()))
            .willReturn(Optional.of(voting)
        );

        //when
        service.cancel(voting.id());

        //then
        assertNotNull(voting.deletedAt());
    }
}