package org.tg.gollaba.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.auth.service.KakaoDisconnectService;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {
    @InjectMocks
    private DeleteUserService service;
    @Mock
    private KakaoDisconnectService kakaoDisconnectService;
    @Mock
    private UserRepository userRepository;

    @Test
    void success(){
        //given
        var user = new UserFixture().build();
        long providerId = Long.parseLong(user.providerId());
        given(userRepository.findById(user.id())).willReturn(Optional.of(user));

        //when
        var throwable = catchThrowable(() -> service.delete(user.id()));

        //then
        assertThat(throwable).isNull();
        verify(kakaoDisconnectService, times(1)).disconnect(providerId);
        verify(userRepository, times(1)).delete(any(User.class));
    }
}