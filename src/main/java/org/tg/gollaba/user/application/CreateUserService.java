package org.tg.gollaba.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.poll.infrastructure.S3Uploader;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final S3Uploader s3Uploader;

    @Transactional
    public Long signup(Requirement requirement){

        var user = createUser(requirement);

        //TODO providerId 체크
        //TODO UserProvider 생성

        userValidator.existedEmail(user);
        userRepository.save(user);
        return user.id();
    }

    private User createUser(Requirement requirement){
        return new User(
            requirement.email,
            requirement.nickName,
            requirement.password,
            requirement.profileImage
                .map(this::upload)
                .orElse(null),
            requirement.backgroundImage
                .map(this::upload)
                .orElse(null)
        );
    }

    private String upload(MultipartFile file){
        return s3Uploader.upload(file, "gollaba-image-bucket");
    }

    public record Requirement(

        String email,

        String nickName,

        String password,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
