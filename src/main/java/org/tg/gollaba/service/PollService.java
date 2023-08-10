package org.tg.gollaba.service;

import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.vo.PollVo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional // TODO: 구현하기
    public PollVo create(CreateRequest request) {
        var poll = request.toEntity();

        pollRepository.save(poll);
        return PollVo.from(poll);
    }

    public record CreateRequest(
            Optional<Long> userId,
//            Long userId,
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
