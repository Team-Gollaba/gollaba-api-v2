package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPollListService {
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Requirement requirement) {
        return pollRepository.findPolls(requirement);
    }

    public record Requirement(
        Optional<OptionGroup> optionGroup,
        Optional<String> query,
        Optional<Boolean> isActive,
        Optional<Poll.PollType> pollType,
        Pageable pageable
    ) {
    }

    public enum OptionGroup {
        TITLE
    }

    public record PollSummary(
        Long id,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endedAt,
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
