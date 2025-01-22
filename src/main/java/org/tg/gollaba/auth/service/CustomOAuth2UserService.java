package org.tg.gollaba.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.client.AppleClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AppleClient appleClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (userRequest.getClientRegistration().getRegistrationId().equals("apple")) {
            var appleUserInfo = appleClient.getUserInfo(userRequest.getAccessToken().getTokenValue());
            Map<String, Object> userAttributes = new HashMap<>() {{
                put("sub", appleUserInfo.id());
                put("email", appleUserInfo.email());
                appleUserInfo.name().ifPresent(name -> put("name", name));
            }};

            return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                userAttributes,
                "sub"
            );
        }

        return super.loadUser(userRequest);
    }
}
