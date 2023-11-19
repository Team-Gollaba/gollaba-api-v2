package org.tg.gollaba.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.poll.infrastructure.S3Uploader;
import org.tg.gollaba.user.domain.User;

import java.util.Optional;

import static org.tg.gollaba.common.support.Status.EMAIL_EXISTED;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository repository;
    private final S3Uploader s3Uploader;

    @Transactional
    public Long signup(Requirement requirement){
        validateEmail(requirement);

        var user = new User(
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

        //TODO providerId 체크
        //TODO UserProvider 생성

        repository.save(user);
        return user.id();
    }

    private String upload(MultipartFile file){
        return s3Uploader.upload(file, "gollaba-image-bucket");
    }

    private void validateEmail(Requirement requirement){
        var isEmail = repository.existsByEmail(requirement.email);

        if (isEmail){
            throw new BadRequestException(EMAIL_EXISTED);
        }
    }

    public record Requirement(

        String email,

        String nickName,

        String password,

        Optional<MultipartFile> profileImage,

        Optional<MultipartFile> backgroundImage
    ){}
}
