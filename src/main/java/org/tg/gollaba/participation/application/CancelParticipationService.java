package org.tg.gollaba.participation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;

import static org.tg.gollaba.common.support.Status.PARTICIPATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CancelParticipationService {
    private final ParticipationRepository participationRepository;

    @Transactional
    public void cancel(Long participantId){ //TODO userId 추가 / 참가자 본인이 아닙니다
        var participation = participationRepository.findById(participantId)
            .orElseThrow(() -> new BadRequestException(PARTICIPATION_NOT_FOUND));

        participation.cancel();

        participationRepository.save(participation);
    }
}
