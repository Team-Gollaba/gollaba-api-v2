package org.tg.gollaba.service;

import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollItem;
import org.tg.gollaba.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.dto.PollDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional
    public Long create(CreateRequirement requirement){
        var poll = createPoll(requirement);

        var pollItems = createPollItems(requirement, poll);
        poll.updatePollItems(pollItems);

        pollRepository.save(poll);
        return  poll.getId();
    }

    private static List<PollItem> createPollItems(CreateRequirement requirement, Poll poll) {
        return requirement.pollItems().stream()
            .map(optionRequirement -> new PollItem(
                poll,
                optionRequirement.description,
                optionRequirement.imageUrl
            ))
            .toList();
    }

    private Poll createPoll(CreateRequirement requirement){
        return new Poll(
            requirement.userId.orElse(null),
            requirement.title,
            requirement.creatorName,
            requirement.pollType,
            requirement.responseType,
            requirement.endedAt.orElse(null)
        );
    }

    public record CreateRequirement(
            Optional<Long> userId,
            String title,
            String creatorName,
            Poll.PollType pollType,
            Poll.PollResponseType responseType,
            List<PollItemRequirement> pollItems,
            Optional<LocalDateTime> endedAt
    ){
        public record PollItemRequirement(
            String description,
            String imageUrl
        ){}
    }
}