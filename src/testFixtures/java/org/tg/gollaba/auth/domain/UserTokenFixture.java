package org.tg.gollaba.auth.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class UserTokenFixture implements TestFixture<UserToken> {
    private Long id = 1L;
    private Long userId = 1L;
    private String refreshToken = "refreshToken-test";

    @Override
    public UserToken build() {
        var user = new UserToken();
        FixtureReflectionUtils.reflect(user, this);
        return user;
    }
}
