package org.tg.gollaba.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.user.component.ProviderDisconnector;
import org.tg.gollaba.user.domain.UserFixture;
import org.tg.gollaba.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {
    @InjectMocks
    private DeleteUserService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProviderDisconnector providerDisconnector;

    @Test
    void success() {
        // given
        var user = new UserFixture().build();
        given(userRepository.findById(user.id()))
            .willReturn(Optional.of(user));

        // when
        var throwable = catchThrowable(() -> service.delete(user.id()));

        // then
        assertThat(throwable).isNull();
        verify(userRepository, times(1)).findById(user.id());
        verify(providerDisconnector, times(1)).disconnect(user.providerType(), user.providerId());
        verify(userRepository, times(1)).delete(user);
    }
}