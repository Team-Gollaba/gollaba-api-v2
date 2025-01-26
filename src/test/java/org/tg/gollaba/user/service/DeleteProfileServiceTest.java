package org.tg.gollaba.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteProfileServiceTest {
    @InjectMocks
    private DeleteProfileService service;

    @Mock
    private UserRepository userRepository;

    @Test
    void success(){
        //given
        var user = new UserFixture()
            .setProfileImageUrl("https://test.com/test.png")
            .build();
        given(userRepository.findById(user.id())).willReturn(Optional.of(user));

        //when
        var throwable = catchThrowable(() -> service.delete(user.id()));

        //then
        assertThat(throwable).isNull();
        var argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        var capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.profileImageUrl()).isNull();

    }
}
