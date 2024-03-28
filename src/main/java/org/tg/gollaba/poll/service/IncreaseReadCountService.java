package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.repository.PollRepository;

@Service
@RequiredArgsConstructor
public class IncreaseReadCountService {

    private final PollRepository pollRepository;

    @Transactional
    public void increase(Long pollId){
        var poll = pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));

        poll.increaseReadCount();
        pollRepository.save(poll);
    }

}
