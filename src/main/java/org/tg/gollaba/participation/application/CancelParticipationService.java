package org.tg.gollaba.participation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelParticipationService {
    private final ParticipationRepository participationRepository;

    @Transactional
    public void cancel(Long participantId){ //TODO userId 추가 / 참가자 본인이 아닙니다
        var participation = participationRepository.findById(participantId)
            .orElse(null);

        participation.cancel();

        participationRepository.save(participation);
    }
}
