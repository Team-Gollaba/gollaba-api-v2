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
public class ChangeProfileService {
    private final FileUploader fileUploader;
    private final UserRepository userRepository;

    @Transactional
    public void changeProfile(Requirement requirement){
        var user = userRepository.findById(requirement.userId())
            .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        var url = fileUploader.uploadProfileImage(user.id(), requirement.profileImage());
        user.changeProfileImage(url);

        userRepository.save(user);
    }

    public record Requirement(
        Long userId,
        MultipartFile profileImage
    ){
    }
}
