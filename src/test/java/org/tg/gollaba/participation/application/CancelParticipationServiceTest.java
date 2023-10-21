package org.tg.gollaba.participation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.participation.domain.ParticipationFixture;


import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CancelParticipationServiceTest {
    @InjectMocks
    private CancelParticipationService service;

    @Mock
    private ParticipationRepository participationRepository;

    @DisplayName("회원이 투표를 철회한다.")
    @Test
    void success(){
        //given
        var participation = new ParticipationFixture().build();

        given(
            participationRepository.findById(participation.id()))
            .willReturn(Optional.of(participation)
        );

        //when
        service.cancel(participation.id());

        //then
        assertNotNull(participation.deletedAt());
    }
}