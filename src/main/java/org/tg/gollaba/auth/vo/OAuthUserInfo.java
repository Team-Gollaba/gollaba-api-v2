package org.tg.gollaba.auth.vo;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.tg.gollaba.user.domain.User;

import java.util.Map;

@Getter
@Accessors(fluent = true)
public class OAuthUserInfo {
    private final String providerId;
    private final User.ProviderType providerType;
    private final String email;
    private final String name;
    private final String profileImageUrl;

    public OAuthUserInfo(String providerId,
                         User.ProviderType providerType,
                         String email,
                         String name,
                         String profileImageUrl) {
        this.providerId = providerId;
        this.providerType = providerType;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public static OAuthUserInfo kakao(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        var accountInfo = (Map<String, Object>) attributes.get("kakao_account");
        var profile = (Map<String, Object>) accountInfo.get("profile");

        return new OAuthUserInfo(
            valueToString(attributes.get("id")),
            User.ProviderType.KAKAO,
            valueToString(accountInfo.get("email")),
            valueToString(profile.get("nickname")),
            valueToString(profile.get("profile_image_url"))
        );
    }

    public static OAuthUserInfo naver(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        var response = (Map<String, Object>) attributes.get("response");

        return new OAuthUserInfo(
            valueToString(response.get("id")),
            User.ProviderType.NAVER,
            valueToString(response.get("email")),
            valueToString(response.get("name")),
            valueToString(response.get("profile_image"))
        );
    }

    public static OAuthUserInfo github(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();

        return new OAuthUserInfo(
            valueToString(attributes.get("id")),
            User.ProviderType.GITHUB,
            valueToString(attributes.get("email")),
            valueToString(attributes.get("name")),
            valueToString(attributes.get("avatar_url"))
        );
    }

    private static String valueToString(Object value) {
        if (value == null) return null;

        return value.toString();
    }
}
