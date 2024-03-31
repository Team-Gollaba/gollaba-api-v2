package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetMyPollsService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        Page<Poll> createdPolls = pollRepository.findAllByUserId(userId, pageable);
        var pollIds = createdPolls.getContent().stream()
            .map(Poll::id)
            .toList();

        var voteCounts = pollRepository.findPollItemIdsAndVoteCounts(pollIds);

        return createdPolls.map(createdPoll -> new PollSummary(
            createdPoll.id(),
            createdPoll.title(),
            createdPoll.creatorName(),
            createdPoll.responseType(),
            createdPoll.pollType(),
            createdPoll.endAt(),
            createdPoll.readCount(),
            0,
            convertPollItemsSummary(createdPoll.items(), voteCounts)
        ));
    }

    private List<PollSummary.PollItem> convertPollItemsSummary(List<PollItem> pollItems,
                                                               Map<Long, Map<Long, Integer>> voteCountsByPollItems) {
        return pollItems.stream()
            .flatMap(item -> voteCountsByPollItems.values().stream()
                .filter(voteCountByPollItem -> voteCountByPollItem.containsKey(item.id()))
                .map(voteCountByPollItem -> {
                    var votingCount = voteCountByPollItem.get(item.id());
                    return new PollSummary.PollItem(
                        item.id(),
                        item.description(),
                        item.imageUrl(),
                        votingCount
                    );
                })
            )
            .toList();
    }
}
