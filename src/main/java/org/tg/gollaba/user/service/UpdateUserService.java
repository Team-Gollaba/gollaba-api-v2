package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.component.FileUploader;
import org.tg.gollaba.user.domain.User;
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

        requirement.profileImage()
            .ifPresent(profileImage -> {
                var url = uploadProfileImage(profileImage, user);
                user.changeProfileImage(url);
            });

        requirement.backgroundImage()
            .ifPresent(backgroundImage -> {
                var url = uploadBackgroundImage(backgroundImage, user);
                user.changeBackgroundImage(url);
            });

        user.update(requirement.name());

        userRepository.save(user);
    }

    private String uploadProfileImage(MultipartFile profileImage, User user){
        return fileUploader.uploadProfileImage(
            user.id() + "-" + UUID.randomUUID(),
            profileImage
        );
    }

    private String uploadBackgroundImage(MultipartFile backgroundImage, User user){
        return fileUploader.uploadBackgroundImage(
            user.id() + "-" + UUID.randomUUID(),
            backgroundImage
        );
    }

    public record Requirement(
        Long userId,
        String name,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
