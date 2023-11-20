package org.tg.gollaba.poll.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.infrastructure.S3Uploader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePollService {
    private final PollRepository pollRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public Long create(Requirement requirement) {
        var items = requirement.items()
            .stream()
            .map(item -> new PollItem(
                item.description(),
                item.imageFile()
                    .map(this::upload)
                    .orElse(null)
            ))
            .toList();
        var poll = createPoll(requirement, items);

        return pollRepository.save(poll).id();
    }

    private String upload(MultipartFile file) {
        return s3Uploader.upload(file, "gollaba-image-bucket");
    }

    private Poll createPoll(Requirement requirement, List<PollItem> items) {
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
            Optional<MultipartFile> imageFile
        ) {
        }
    }
}
