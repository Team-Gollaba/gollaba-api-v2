package org.tg.gollaba.user.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class UserFixture implements TestFixture<User> {
    private Long id = 1L;
    private String email = "test@test.com";
    private String name = "test";
    private String password = "test1234**";
    private String profileImageUrl = "https://test.com/test.png";
    private String backgroundImageUrl = "https://test.com/test.png";
    private User.RoleType roleType = User.RoleType.USER;
    private User.ProviderType providerType = User.ProviderType.KAKAO;
    private String providerId = "providerId-test";

    @Override
    public User build() {
        var user = new User();
        FixtureReflectionUtils.reflect(user, this);
        return user;
    }
}
