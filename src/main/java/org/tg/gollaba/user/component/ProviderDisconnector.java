package org.tg.gollaba.user.component;

import org.tg.gollaba.user.domain.User;

public interface ProviderDisconnector {

    void disconnect(User.ProviderType providerType, String providerId);
}
