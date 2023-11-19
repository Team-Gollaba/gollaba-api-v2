package org.tg.gollaba.user.application;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.poll.infrastructure.S3Uploader;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {
    @InjectMocks
    private CreateUserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Test
    void signup() {
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var requirement = new RequirementFixture()
            .setProfileImage(Optional.ofNullable(mockImageFile))
            .setBackgroundImage(Optional.ofNullable(mockImageFile))
            .build();
        var user = new UserFixture().build();
        given(userRepository.save(any(User.class)))
            .willReturn(user);

        //when
        var throwable = catchThrowable(() -> service.signup(requirement));

        //then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @DisplayName("이미지를 기입하지 않은 경우, 예외가 발생하지 않는다.")
    @Test
    void imageIsNull(){
        var requirement = new RequirementFixture()
            .setProfileImage(Optional.ofNullable(null))
            .setBackgroundImage(Optional.ofNullable(null))
            .build();
        var user = new UserFixture().build();
        given(userRepository.save(any(User.class)))
            .willReturn(user);

        //when
        var throwable = catchThrowable(() -> service.signup(requirement));

        //then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).save(any(User.class));

    }

    @DisplayName("중복된 이메일로 가입할 경우 예외가 발생한다.")
    @Test
    void isEmailDuplicate(){
        //given
        var userFixture = new UserFixture();
        var signedUser = userFixture.build();
        given(userRepository.existsByEmail(signedUser.email()))
            .willReturn(true);
        var requirement = new RequirementFixture()
            .setEmail(signedUser.email())
            .build();

        //when
        var throwable = catchThrowable(() -> service.signup(requirement));

        //then
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class RequirementFixture implements TestFixture<CreateUserService.Requirement> {
        private String email = "water";
        private String nickName = "digital-hamster";
        private String password = "pw";
        private Optional<MultipartFile> profileImage = Optional.empty();

        private Optional<MultipartFile> backgroundImage = Optional.empty();

        @Override
        public CreateUserService.Requirement build() {
            return new CreateUserService.Requirement(
                email,
                nickName,
                password,
                profileImage,
                backgroundImage
            );
        }
    }
}