package org.tg.gollaba.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.user.component.FileUploaderImpl;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateUserService {
    private final UserRepository userRepository;
    private final FileUploaderImpl fileUploader;

    @Transactional
    public void update(Requirement requirement, AuthenticatedUser authenticatedUser) {
        var user = userRepository.findByEmail(authenticatedUser.email())
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        user.update(
            requirement.name,
            profileUpload(requirement),
            backgroundImageUpload(requirement)
        );

        userRepository.save(user);
    }

    private String profileUpload(Requirement requirement){
        var profileFileName = requirement.profileImage
            .map(MultipartFile::getOriginalFilename).orElse(null);

        return requirement.profileImage
            .map(file -> fileUploader. //여기서 에러터짐
                uploadProfileImage(profileFileName, file)).orElse(null);
    }

    private String backgroundImageUpload(Requirement requirement){
        var backgroundFileName = requirement.backgroundImage
            .map(MultipartFile::getOriginalFilename).orElse(null);

        return requirement.backgroundImage
            .map(file -> fileUploader.
                uploadBackgroundImage(backgroundFileName, file)).orElse(null);
    }

    public record Requirement(
        String name,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
