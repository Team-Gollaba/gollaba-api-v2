package org.tg.gollaba.auth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.tg.gollaba.user.domain.User;

@Component
@RequiredArgsConstructor
public class OAuthClientRegistrationProvider {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public ClientRegistration findByProviderType(User.ProviderType providerType) {
        return clientRegistrationRepository.findByRegistrationId(providerType.name().toLowerCase());
    }
}
