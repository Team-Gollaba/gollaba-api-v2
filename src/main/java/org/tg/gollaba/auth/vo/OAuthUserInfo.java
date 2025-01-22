package org.tg.gollaba.auth.vo;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.tg.gollaba.user.domain.User;

import java.util.Map;

public record OAuthUserInfo(
    String providerId,
    User.ProviderType providerType,
    String email,
    String name,
    String profileImageUrl
) {
    public static OAuthUserInfo of(OAuth2User oAuth2User,
                                   User.ProviderType providerType) {
        return switch (providerType) {
            case KAKAO -> kakao(oAuth2User);
            case NAVER -> naver(oAuth2User);
            case GITHUB -> github(oAuth2User);
            case APPLE -> apple(oAuth2User);
        };
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
    public static OAuthUserInfo apple(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();

        return new OAuthUserInfo(
            valueToString(attributes.get("sub")),
            User.ProviderType.APPLE,
            valueToString(attributes.get("email")),
            valueToString(attributes.get("name")),
            null
        );
    }

    private static String valueToString(Object value) {
        if (value == null) return null;

        return value.toString();
    }
}
