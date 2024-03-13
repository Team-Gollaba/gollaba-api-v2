package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.component.FileUploader;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserService {
    private final UserRepository userRepository;
    private final FileUploader fileUploader;

    @Transactional
    public void update(Requirement requirement) {
        var user = userRepository.findById(requirement.userId())
                .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        var profileUrl  = requirement.profileImage()
            .map(file -> {
                var timestamp = String.valueOf(System.currentTimeMillis());
                return fileUploader.uploadProfileImage(requirement.userId()
                    + "-" + UUID.randomUUID()
                    + "-" + timestamp,
                    file);
            })
            .orElse(null);

            var backgroundUrl  = requirement.backgroundImage()
            .map(file -> {
                var timestamp = String.valueOf(System.currentTimeMillis());
                return fileUploader.uploadBackgroundImage(requirement.userId()
                    + "-" + UUID.randomUUID()
                    + "-" + timestamp,
                    file);
            })
            .orElse(null);

        user.update(
            profileUrl,
            backgroundUrl,
            requirement.name()
        );

        userRepository.save(user);
    }

    public record Requirement(
        Long userId,
        String name,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
