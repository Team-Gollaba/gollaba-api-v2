package org.tg.gollaba.service;

import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.repository.PollRepository;
import org.tg.gollaba.vo.PollVo;
import org.tg.gollaba.common.support.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional // TODO: 구현하기
    public void create(CreateRequest request) {
        Poll poll = null;

        pollRepository.save(poll);
    }

    record CreateRequest(
    ) {
    }
}
