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
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.voting.repository.VotingItemRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Service
@RequiredArgsConstructor
public class GetMyPollsService {
    private final PollRepository pollRepository;
    private final VotingItemRepository votingItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        var polls = pollRepository.findMyPolls(userId, pageable);
        var pollIds = polls.stream().map(Poll::id).collect(Collectors.toList());

        var pollItemIds = polls.stream()
            .flatMap(poll -> poll.items().stream())
            .map(PollItem::id)
            .distinct()
            .toList();

        var votingCountByItems = votingItemRepository.votingCountByPollItemIds(pollItemIds);
        var votingCountMapByPollId = combine(polls, votingCountByItems);
        var pollIdUserProfileUrlMap = getPollIdToUserProfileMap(pollIds, pollRepository, userRepository);

        return PollSummary.convertToPage(
            polls,
            pollIdUserProfileUrlMap,
            votingCountMapByPollId,
            pageable
        );
    }

    private static Map<Long, String> getPollIdToUserProfileMap(List<Long> pollIds,
                                                               PollRepository pollRepository,
                                                               UserRepository userRepository) {
        var pollUserMap = pollRepository.findUserIdsByPollIds(pollIds);
        var userIds = pollUserMap.values().stream().collect(Collectors.toList());
        var userProfileUrlMap = userRepository.findProfileImagesByUserIds(userIds);

        return pollUserMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> userProfileUrlMap.getOrDefault(entry.getValue(), "")
            ));
    }

    private Map<Long, Map<Long, Integer>> combine(Page<Poll> polls,
                                                  Map<Long, Integer> votingCountByItems) {
        return polls.stream()
            .collect(toMap(
                Poll::id,
                poll -> poll.items().stream()
                    .collect(toMap(
                        PollItem::id,
                        item -> votingCountByItems.getOrDefault(item.id(), 0)
                    ))
            ));
    }
}
