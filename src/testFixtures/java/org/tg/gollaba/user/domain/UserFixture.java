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
   private String email = "waterlove1439@naver.com";
   private String nickName = "digital-hamster";
   private String password = "123123";
   private String profileImageUrl;
   private String backgroundImageUrl;

    @Override
    public User build(){
        var user = new User();
        FixtureReflectionUtils.reflect(user, this);
        return user;
    }
}
