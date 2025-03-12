package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.config.CacheKeys;
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
    @Cacheable(
        value = CacheKeys.POLL_LIST_HOME, 
        key = "'page-' + #requirement.pageable.pageNumber + '-size-' + #requirement.pageable.pageSize",
        condition = """
            #requirement.query().isEmpty() &&
            #requirement.userId().isEmpty() &&
            #requirement.isActive().isEmpty() &&
            #requirement.pollType().isEmpty() &&
            #requirement.pageable.pageNumber <= 3 &&
            #requirement.pageable.pageSize <= 20
            """
    )
    public PageResponse<PollSummary> get(Requirement requirement) {
        return getResult(requirement);
    }

    @Transactional
    @CachePut(
        value = CacheKeys.POLL_LIST_HOME,
        key = "'page-' + #requirement.pageable.pageNumber + '-size-' + #requirement.pageable.pageSize",
        condition = """
            #requirement.query().isEmpty() &&
            #requirement.userId().isEmpty() &&
            #requirement.isActive().isEmpty() &&
            #requirement.pollType().isEmpty() &&
            #requirement.pageable.pageNumber <= 3 &&
            #requirement.pageable.pageSize <= 20
            """
    )
    public PageResponse<PollSummary> refresh(Requirement requirement) {
        return getResult(requirement);
    }

    private PageResponse<PollSummary> getResult(Requirement requirement) {
        requirement.optionGroup
            .filter(optionGroup -> optionGroup == OptionGroup.TITLE)
            .ifPresent(optionGroup -> {
                var pollSearchStat = new PollSearchStat(
                    requirement.query.orElseThrow(),
                    requirement.userId().orElse(null)
                );
                pollSearchStatRepository.save(pollSearchStat);
            });

        return PageResponse.from(pollRepository.findPollList(requirement));
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
