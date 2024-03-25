package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMyPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        Page<Poll> createdPolls = pollRepository.findAllByUserId(userId, pageable);

        return createdPolls.map(createdPoll -> {
            var voteCounts = pollRepository.findVoteCounts(createdPoll.id());

            return new PollSummary(
                createdPoll.id(),
                createdPoll.title(),
                createdPoll.creatorName(),
                createdPoll.responseType(),
                createdPoll.pollType(),
                createdPoll.endAt(),
                createdPoll.readCount(),
                convertPollItemsSummary(createdPoll.items(), voteCounts)
            );
        });
    }

    private List<PollSummary.PollItem> convertPollItemsSummary(List<PollItem> pollItems, Map<Long, Long> voteCounts) {
        return pollItems.stream()
            .map(item -> new PollSummary.PollItem(
                item.id(),
                item.description(),
                item.imageUrl(),
                voteCounts.getOrDefault(item.id(), 0L).intValue()
            ))
            .collect(Collectors.toList());
    }

        public record PollSummary(
        Long id,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer readCount,
        List<PollItem> items
    ) {
        public record PollItem(
            Long id,
            String description,
            String imageUrl,
            Integer voteCount
        ) {
        }
    }
}
