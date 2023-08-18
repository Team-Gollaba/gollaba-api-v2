package org.tg.gollaba.service;

import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.dto.PollDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional
    public Long create(CreateRequirement requirement){
        var poll = createPoll(requirement);
        var pollOptions = createPollOptions(requirement, poll);

        poll.addPollOptions(pollOptions);
        pollRepository.save(poll); //여기서 터짐
        
        return  poll.getId();
    }

    private static List<PollOption> createPollOptions(CreateRequirement requirement, Poll poll) {
        return requirement.pollOptions()
            .stream()
            .map(optionRequirement -> new PollOption(
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
            requirement.responseType
        );
    }

    public record CreateRequirement(
            Optional<Long> userId,
            String title,
            String creatorName,
            Poll.PollType pollType,
            Poll.PollResponseType responseType,
            List<PollOptionRequirement> pollOptions
    ) {
        public record PollOptionRequirement(
            String description,
            String imageUrl
        ){}
    }
}