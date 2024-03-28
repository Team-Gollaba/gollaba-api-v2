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
            .ifPresent(image -> {
                var url = fileUploader.uploadProfileImage(user.id(), image);
                user.changeProfileImage(url);
            });

        requirement.backgroundImage()
            .ifPresent(image -> {
                var url = fileUploader.uploadBackgroundImage(user.id(), image);
                user.changeBackgroundImage(url);
            });

        user.update(requirement.name());
        userRepository.save(user);
    }

    public record Requirement(
        Long userId,
        String name,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
