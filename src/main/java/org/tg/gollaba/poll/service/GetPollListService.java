package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.domain.PollSearchStats;
import org.tg.gollaba.stats.repository.PollSearchStatsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPollListService {
    private final PollRepository pollRepository;
    private final PollSearchStatsRepository pollSearchStatsRepository;

    @Transactional
    public Page<PollSummary> get(Requirement requirement) {
        requirement.optionGroup
            .filter(optionGroup -> optionGroup == OptionGroup.TITLE)
            .ifPresent(optionGroup -> {
                var pollSearchStats = new PollSearchStats(requirement.query.orElseThrow());
                pollSearchStatsRepository.save(pollSearchStats);
            });

        return pollRepository.findPollList(requirement);
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
}
