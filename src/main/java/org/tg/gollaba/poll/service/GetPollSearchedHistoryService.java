package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.stats.repository.PollSearchStatRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetPollSearchedHistoryService {
    private final PollSearchStatRepository pollSearchStatRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> get(){
        return pollSearchStatRepository.findTop10SearchedWords();
    }
}
