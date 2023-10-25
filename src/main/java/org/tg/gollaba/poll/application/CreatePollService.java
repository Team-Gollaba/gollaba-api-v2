package org.tg.gollaba.poll.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePollService {
    private final PollRepository pollRepository;

    @Transactional
    public Long create(Requirement requirement) {
        var poll = createPoll(requirement);

        return pollRepository.save(poll).id();
    }

    private Poll createPoll(Requirement requirement) {
        var items = requirement.items()
            .stream()
            .map(item -> new PollItem(
                item.description(),
                null // TODO: 투표 항목 이미지 기능 구현
            ))
            .toList();

        return new Poll(
            requirement.userId(),
            requirement.title(),
            requirement.creatorName(),
            requirement.responseType(),
            requirement.pollType(),
            requirement.endedAt()
                .orElse(null),
            items
        );
    }

    public record Requirement(
        Long userId,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        Optional<LocalDateTime> endedAt,
        List<Item> items
    ) {
        public record Item(
            String description,
            MultipartFile imageFile
        ) {
        }
    }
}
