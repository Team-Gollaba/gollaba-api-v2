package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.repository.PollStatsRepository;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.voting.repository.VotingItemRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class GetTopPollsService {
    private final PollRepository pollRepository;
    private final VotingItemRepository votingItemRepository;
    private final PollStatsRepository pollStatsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PollSummary> get(int limit){
        var aggregationDate = LocalDate.now();

        var pollIds = pollStatsRepository.findTopPollIds(aggregationDate, limit);
        var polls = pollRepository.findAllById(pollIds); //findAllById > 순서가 뒤죽박죽으로 가져와짐
        var sortedPolls = pollIds.stream() //해당 순서에 맞게 pollIds 기준으로 재배치
            .map(id -> polls.stream()
                .filter(poll -> poll.id().equals(id))
                .findFirst().orElse(null))
            .collect(Collectors.toList());

        var pollItemIds = sortedPolls.stream()
            .flatMap(poll -> poll.items().stream())
            .map(PollItem::id)
            .distinct()
            .toList();

        var votingCountByItems = votingItemRepository.votingCountByPollItemIds(pollItemIds);
        var votingCountMapByPollId = combine(sortedPolls, votingCountByItems);
        var pollIdUserProfileUrlMap = getPollIdToUserProfileMap(pollIds, pollRepository, userRepository);

        return PollSummary.convertToList(
            sortedPolls,
            pollIdUserProfileUrlMap,
            votingCountMapByPollId
        );
    }

    private static Map<Long, String> getPollIdToUserProfileMap(List<Long> pollIds,
                                                               PollRepository pollRepository,
                                                               UserRepository userRepository) {
        var pollUserMap = pollRepository.findUserIdsByPollIds(pollIds);
        var userIds = pollUserMap.values().stream().collect(Collectors.toList());
        var userProfileMap = userRepository.findProfileImagesByUserIds(userIds);

        return pollUserMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> userProfileMap.getOrDefault(entry.getValue(), "")
            ));
    }

    private Map<Long, Map<Long, Integer>> combine(List<Poll> polls,
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