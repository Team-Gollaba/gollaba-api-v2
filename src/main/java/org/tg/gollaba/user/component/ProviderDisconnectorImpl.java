package org.tg.gollaba.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.client.KakaoClient;
import org.tg.gollaba.user.domain.User;

@Component
@RequiredArgsConstructor
public class ProviderDisconnectorImpl implements ProviderDisconnector {
    private final KakaoClient kakaoClient;

    @Override
    public void disconnect(User.ProviderType providerType, String providerId) {
        switch (providerType) {
            case KAKAO -> kakaoClient.disconnect(providerId);
            case APPLE -> {} // TODO: implement
            case NAVER -> {} // TODO: implement
            default -> throw new IllegalArgumentException("Unknown provider type: " + providerType);
        }
    }
}
