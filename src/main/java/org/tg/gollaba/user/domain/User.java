package org.tg.gollaba.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private RoleType roleType;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    private User.ProviderType providerType;

    @Column(name = "provider_id")
    private String providerId;

    public User(String email,
                String name,
                String password,
                String profileImageUrl,
                RoleType roleType,
                User.ProviderType providerType,
                String providerId) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.roleType = roleType;
        this.providerType = providerType;
        this.providerId = providerId;
    }

    public void updateBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public enum RoleType {
        USER,
        ADMIN
    }

    public enum ProviderType {
        KAKAO,
        NAVER
    }
}
