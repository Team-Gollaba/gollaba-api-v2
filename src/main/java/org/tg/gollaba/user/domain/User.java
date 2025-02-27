package org.tg.gollaba.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.support.StringUtils;
import org.tg.gollaba.common.support.ValidationUtils;

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
        validate();
    }

    private void validate() {
        if (providerType != null && providerId == null
            || providerType == null && providerId != null) {
            throw new IllegalArgumentException("providerType, providerId 둘 다 필요합니다.");
        }

        if (providerType == null && password == null) {
            throw new IllegalArgumentException("password 는 필수입니다.");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
    }

    public void update(String name){
        assert name != null;
        this.name = name;
    }

    public void deleteProfileImage(){
        this.profileImageUrl = null;
    }

    public void changeProfileImage(String profileImageUrl){
            if (!ValidationUtils.isValidUrl(profileImageUrl)) {
                throw new IllegalArgumentException("프로필 이미지 URL 형식이 올바르지 않습니다.");
            }
            this.profileImageUrl = profileImageUrl;
    }

    public enum RoleType {
        USER,
        ADMIN
    }

    public enum ProviderType {
        KAKAO("카카오"),
        NAVER("네이버"),
        GITHUB("깃허브"),
        APPLE("애플");

        private final String description;

        ProviderType(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }
    }
}