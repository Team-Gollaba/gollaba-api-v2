package org.tg.gollaba.poll.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.controller.serializer.HashIdSerializer;
import org.tg.gollaba.poll.domain.Poll;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

public record PollSummary(
    @JsonSerialize(using = HashIdSerializer.class)
    Long id,
    String title,
    String creatorName,
    String creatorProfileUrl,
    Poll.PollResponseType responseType,
    Poll.PollType pollType,
    LocalDateTime endAt,
    Integer readCount,
    Integer totalVotingCount,
    List<PollItem> items
) {
    public record PollItem(
        Long id,
        String description,
        String imageUrl,
        Integer votingCount
    ) {
    }
    private static PollSummary from(Poll poll,
                                    Map<Long, String> pollIdUserProfileUrlMap,
                                    Map<Long, Integer> votingCountMap) {
        var totalVotingCount = votingCountMap.values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();

        var items = poll.items()
            .stream()
            .map(item -> new PollSummary.PollItem(
                item.id(),
                item.description(),
                item.imageUrl(),
                votingCountMap.getOrDefault(item.id(), 0)
            ))
            .toList();

        var creatorProfileUrl = pollIdUserProfileUrlMap.get(poll.id());
        if ("".equals(creatorProfileUrl)) {
            creatorProfileUrl = null;  // "" >> null 변환
        }

        return new PollSummary(
            poll.id(),
            poll.title(),
            poll.creatorName(),
            creatorProfileUrl,
            poll.responseType(),
            poll.pollType(),
            poll.endAt(),
            poll.readCount(),
            totalVotingCount,
            items
        );
    }

    public static List<PollSummary> convertToList(List<Poll> polls,
                                                  Map<Long, String> pollIdUserProfileUrlMap,
                                                  Map<Long, Map<Long, Integer>> votingCountMapByPollId) {
        return polls.stream()
            .map(poll -> {
                var votingCountMap = votingCountMapByPollId.getOrDefault(poll.id(), emptyMap());
                return from(poll, pollIdUserProfileUrlMap, votingCountMap);
            })
            .toList();
    }

    public static Page<PollSummary> convertToPage(Page<Poll> polls,
                                                  Map<Long, String> pollIdUserProfileUrlMap,
                                                  Map<Long, Map<Long, Integer>> votingCountMapByPollId,
                                                  Pageable pageable) {
        var summaries = polls.stream()
            .map(poll -> {
                var votingCountMap = votingCountMapByPollId.getOrDefault(poll.id(), emptyMap());
                return from(poll, pollIdUserProfileUrlMap, votingCountMap);
            })
            .toList();

        return new PageImpl<>(summaries, pageable, polls.getTotalElements());
    }
}
