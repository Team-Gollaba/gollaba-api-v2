package org.tg.gollaba.participation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.participation.domain.ParticipationFixture;
import org.tg.gollaba.participation.domain.ParticipationItemFixture;
import org.tg.gollaba.poll.domain.PollFixture;


import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelParticipationServiceTest {
    @InjectMocks
    private CancelParticipationService service;

    @Mock
    private ParticipationRepository participationRepository;

    @BeforeEach
    void setUp() {
        participationRepository = Mockito.mock(ParticipationRepository.class);
        service = new CancelParticipationService(participationRepository);
    }

    @DisplayName("회원이 투표를 철회한다.")
    @Test
    void success(){
        //given
        var poll = new PollFixture()
            .setId(1L)
            .build();

        var participation = new ParticipationFixture()
            .setId(1L)
            .setPoll(poll)
            .setUserId(1L)
            .setItems(Set.of(new ParticipationItemFixture().setId(1L).build()))
            .build();

        when(participationRepository
            .findById(participation.id()))
            .thenReturn(Optional.of(participation));

        //when
        service.cancel(participation.id());

        //then
        verify(participationRepository, times(1)).delete(participation);
    }

    @DisplayName("비회원이 투표 철회 요청을 넣으면 예외가 발생한다.")
    @Test
    void cancelToAnonymous(){
        assertThrows(
            BadRequestException.class,
            () -> service.cancel(null)
        );
    }
}