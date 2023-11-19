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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRoleType userRole;

    public User(String email, String nickName, String password, String profileImageUrl, String backgroundImageUrl) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.userRole = UserRoleType.ROLE_USER;
    }



}
