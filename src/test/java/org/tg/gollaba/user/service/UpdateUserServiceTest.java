package org.tg.gollaba.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.user.component.FileUploader;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @InjectMocks
    private UpdateUserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploader fileUploader;


    @Test
    void success() {
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var user = new UserFixture().build();

        var requirement = new UpdateUserService.Requirement(
            1L,
            "updateName",
            Optional.of(mockImageFile),
            Optional.of(mockImageFile)
        );
        given(userRepository.findById(requirement.userId())).willReturn(Optional.of(user));
        given(fileUploader.uploadProfileImage(any(), eq(mockImageFile))).willReturn("https://updated-url-1.com/test.png");
        given(fileUploader.uploadBackgroundImage(any(), eq(mockImageFile))).willReturn("https://updated-url-2.com/test.png");

        //when
        var throwable = catchThrowable(() -> service.update(requirement));

        //then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).save(any(User.class));

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.name()).isEqualTo(requirement.name());
        assertThat(capturedUser.profileImageUrl()).isNotNull().isEqualTo("https://updated-url-1.com/test.png");
        assertThat(capturedUser.backgroundImageUrl()).isNotNull().isEqualTo("https://updated-url-2.com/test.png");
    }
}