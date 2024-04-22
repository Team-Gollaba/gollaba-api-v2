package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePollService {
    private final PollRepository pollRepository;

    @Transactional
    public void update(Requirement requirement){
        var poll = pollRepository.findMyPoll(requirement.pollId(), requirement.userId)
            .orElseThrow(() -> new BadRequestException(Status.POLL_NOT_FOUND));

        updatePollItem(poll, requirement);

        poll.update(
            requirement.title(),
            requirement.endAt()
        );

        pollRepository.save(poll);
    }

    private void updatePollItem(Poll poll, Requirement requirement){
        for (int i = 0; i < requirement.items().size(); i++) {
            var requirementItem = requirement.items().get(i);

            poll.updatePollItem(
                poll.items().get(i).id(),
                requirementItem.description(),
                requirementItem.image()
                );
            }
        }

    public record Requirement(
        Long userId,
        Long pollId,
        String title,
        LocalDateTime endAt,
        List<Item> items
    ){
        public record Item(
            String description,
            String image
        ){
        }
    }
}
