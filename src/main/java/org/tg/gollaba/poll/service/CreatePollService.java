package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.poll.component.FileUploader;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePollService {
    private final PollRepository pollRepository;
    private final FileUploader fileUploader;

    @Transactional
    public long create(Requirement requirement) {
        var items = requirement.items()
            .stream()
            .map(Requirement.Item::description)
            .map(PollItem::new)
            .toList();
        var poll = createPoll(requirement, items);

        var savedPoll = pollRepository.save(poll);
        uploadPollItemImages(savedPoll, requirement.items());

        return savedPoll.id();
    }

    private Poll createPoll(Requirement requirement,
                            List<PollItem> items) {
        return new Poll(
            requirement.userId(),
            requirement.title(),
            requirement.creatorName(),
            requirement.responseType(),
            requirement.pollType(),
            requirement.endAt()
                .orElse(null),
            items
        );
    }

    private void uploadPollItemImages(Poll poll, List<Requirement.Item> requirementItems) {
        var pollItems = poll.items();

        for (int i = 0; i < requirementItems.size(); i++) {
            var imageFile = requirementItems.get(i).imageFile();

            if (imageFile.isEmpty()) {
                continue;
            }

            var pollItem = pollItems.get(i);
            var imageUrl = fileUploader.uploadPollItemImage(
                poll.id(),
                pollItem.id(),
                imageFile.get()
            );

            pollItem.changeImageUrl(imageUrl);
        }
    }

    public record Requirement(
        Long userId,
        String title,
        String creatorName,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        Optional<LocalDateTime> endAt,
        List<Item> items
    ) {
        public record Item(
            String description,
            Optional<MultipartFile> imageFile
        ) {
        }
    }
}
