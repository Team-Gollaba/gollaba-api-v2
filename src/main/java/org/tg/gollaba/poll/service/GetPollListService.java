package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.stats.domain.PollSearchStat;
import org.tg.gollaba.stats.repository.PollSearchStatRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPollListService {
    private final PollRepository pollRepository;
    private final PollSearchStatRepository pollSearchStatRepository;

    @Transactional
    public Page<PollSummary> get(Requirement requirement) {
        requirement.optionGroup
            .filter(optionGroup -> optionGroup == OptionGroup.TITLE)
            .ifPresent(optionGroup -> {
                var pollSearchStat = new PollSearchStat(
                    requirement.query.orElseThrow(),
                    requirement.userId().orElse(null)
                );
                pollSearchStatRepository.save(pollSearchStat);
            });

        return pollRepository.findPollList(requirement);
    }

    public record Requirement(
        Optional<OptionGroup> optionGroup,
        Optional<String> query,
        Optional<Boolean> isActive,
        Optional<Poll.PollType> pollType,
        Pageable pageable,
        Optional<Long> userId
    ) {
    }

    public enum OptionGroup {
        TITLE
    }
}
