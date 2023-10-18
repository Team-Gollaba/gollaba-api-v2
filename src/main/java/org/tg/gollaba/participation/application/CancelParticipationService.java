package org.tg.gollaba.participation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.participation.domain.Participation;

import static org.tg.gollaba.common.support.Status.*;

@Service
@RequiredArgsConstructor
public class CancelParticipationService {
    private final ParticipationRepository participationRepository;

    @Transactional
    public void cancel(Long participantId){ //TODO userId 추가 / 참가자 본인이 아닙니다
        var participation = participationRepository.findById(participantId)
            .orElseThrow(() -> new BadRequestException(PARTICIPATION_NOT_FOUND));

        isAnonymous(participation);

        participationRepository.delete(participation);
    }

    private void isAnonymous(Participation participation){
        if(participation.userId() == null){
            throw new ParticipationValidator.InvalidParticipationException(ANONYMOUS_NOT_CANCEL);
        }
    }
}
