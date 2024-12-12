package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPollDetailsService {
    private final PollRepository pollRepository;
    private final VotingRepository votingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PollDetails get(long pollId) {
        var poll = pollRepository.findById(pollId)
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));

        var totalVotingCount = votingRepository.votingCountMapByPollId(List.of(pollId))
            .getOrDefault(pollId, Collections.emptyMap())
            .values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();

        var creatorProfileUrl = userRepository
            .findProfileImageUrlByUserId(poll.userId())
            .orElse(null);

        return new GetPollDetailsService.PollDetails(
            poll.id(),
            poll.title(),
            poll.creatorName(),
            creatorProfileUrl,
            poll.responseType(),
            poll.pollType(),
            poll.endAt(),
            totalVotingCount,
            poll.readCount(),
            poll.items()
                .stream()
                .map(item -> new GetPollDetailsService.PollDetails.PollItem(
                    item.id(),
                    item.description(),
                    item.imageUrl(),
                    votingRepository.votingCountMapByPollId(List.of(pollId))
                        .getOrDefault(pollId, Collections.emptyMap())
                        .getOrDefault(item.id(), 0)
                ))
                .toList()
        );
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
