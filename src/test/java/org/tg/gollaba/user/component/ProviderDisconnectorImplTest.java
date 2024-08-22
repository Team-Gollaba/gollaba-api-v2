package org.tg.gollaba.user.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.client.KakaoClient;
import org.tg.gollaba.user.domain.User.ProviderType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProviderDisconnectorImplTest {
    @InjectMocks
    private ProviderDisconnectorImpl providerDisconnector;
    @Mock
    private KakaoClient kakaoClient;

    @Test
    void success() {
        // given
        var providerId = "123";
        var providerType = ProviderType.KAKAO;

        // when
        var throwable = catchThrowable(() -> providerDisconnector.disconnect(providerType, providerId));

        // then
        assertThat(throwable).isNull();
        verify(kakaoClient, times(1)).disconnect(providerId);
    }
}