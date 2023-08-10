package org.tg.gollaba.service;

import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.dto.PollDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional // TODO: 구현하기
    public PollDto create(CreateRequirement request) {
        var poll = request.toEntity();

        pollRepository.save(poll);
        return PollDto.from(poll);
    }

    public record CreateRequirement(
            Optional<Long> userId, //creatorId리
            String title,
            String creatorName,
            Poll.PollType pollType,
            Poll.PollResponseType responseType

    ) {
        public  Poll toEntity() {
            return new Poll(
                userId.orElse(null),//userId,
                title,
                creatorName,
                pollType,
                responseType);
        }


    }
}
