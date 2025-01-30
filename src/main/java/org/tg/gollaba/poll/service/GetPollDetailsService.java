package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPollDetailsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public PollDetails get(long pollId) {
        return pollRepository.findPollDetails(pollId);
    }

    public record PollDetails(
        Long id,
        String title,
        String creatorName,
        String creatorProfileUrl,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer totalVotingCount,
        Integer votedPeopleCount,
        Integer readCount,
        List<PollItem> items
    ) {
        public record PollItem(
            Long id,
            String description,
            String imageUrl,
            Integer votingCount
        ) {
        }
    }
}
