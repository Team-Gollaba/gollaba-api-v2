package org.tg.gollaba.user.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.user.component.FileUploader;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeProfileServiceTest {
    @InjectMocks
    private ChangeProfileService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploader fileUploader;

    @Test
    void success(){
        //given
        var user = new UserFixture().build();
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var requirement = new ChangeProfileService.Requirement(
            1L,
            mockImageFile
        );
        given(userRepository.findById(requirement.userId())).willReturn(Optional.of(user));
        given(fileUploader.uploadProfileImage(anyLong(), eq(mockImageFile))).willReturn("https://updated-url.com/test.png");

        //when
        var throwable = catchThrowable(() -> service.changeProfile(requirement));

        //then
        assertThat(throwable).isNull();
        verify(fileUploader, times(1)).uploadProfileImage(
            user.id(),
            requirement.profileImage()
        );

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.profileImageUrl()).isEqualTo("https://updated-url.com/test.png");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RequirementFixture implements TestFixture<ChangeProfileService.Requirement> {
        private Long userId = 1L;
        private MultipartFile mockImageFile = Mockito.mock(MultipartFile.class);

        @Override
        public ChangeProfileService.Requirement build() {
            return new ChangeProfileService.Requirement(
                userId,
                mockImageFile
            );
        }
    }
}