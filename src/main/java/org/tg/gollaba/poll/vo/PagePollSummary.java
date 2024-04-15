package org.tg.gollaba.poll.vo;

import org.tg.gollaba.poll.domain.Poll;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record PagePollSummary(
    String id,
    String title,
    String creatorName,
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
        public static List<PagePollSummary.PollItem> to(List<PollSummary.PollItem> pollItems) {

            return pollItems.stream()
                .map(pollItem -> new PagePollSummary.PollItem(
                    pollItem.id(),
                    pollItem.description(),
                    pollItem.imageUrl(),
                    pollItem.votingCount()
                ))
                .collect(Collectors.toList());
        }
    }
}
