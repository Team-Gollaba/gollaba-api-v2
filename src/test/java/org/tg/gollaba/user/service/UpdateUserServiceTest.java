package org.tg.gollaba.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.auth.controller.TestTokenController;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.user.component.FileUploaderImpl;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private FileUploaderImpl fileUploader;


    @Test
    void success() {
        //given
        var mockImageFile = Mockito.mock(MultipartFile.class);
        var user = new UserFixture().build();
        var authenticatedUser = new AuthenticatedUser(user.id(),
                                                      user.name(),
                                                      user.email());

        var requirement = new UpdateUserService.Requirement(
            "updateName",
            Optional.of(mockImageFile),
            Optional.of(mockImageFile)
        );
        given(userRepository.findByEmail(user.email())).willReturn(Optional.of(user));
        given(fileUploader.uploadProfileImage(any(), any())).willReturn("https://updated-url-1.com/test.png");
        given(fileUploader.uploadBackgroundImage(any(), any())).willReturn("https://updated-url-2.com/test.png");

        //when
        service.update(requirement, authenticatedUser);

        //then
        verify(userRepository, times(1)).save(any(User.class));

        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.name()).isEqualTo(requirement.name());
        assertThat(capturedUser.profileImageUrl()).isEqualTo("https://updated-url-1.com/test.png");
        assertThat(capturedUser.backgroundImageUrl()).isEqualTo("https://updated-url-2.com/test.png");
    }

    private FileUploaderImpl fileUploader(String fileName) {
        FileUploaderImpl mockFileUploader = Mockito.mock(FileUploaderImpl.class);
        return mockFileUploader;
    }
}