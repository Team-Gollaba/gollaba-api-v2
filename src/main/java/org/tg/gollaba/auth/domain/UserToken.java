package org.tg.gollaba.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntityForOnlyCreatedAt;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken extends BaseEntityForOnlyCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    public UserToken(Long userId,
                     String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
