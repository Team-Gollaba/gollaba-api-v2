package org.tg.gollaba.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class UserTest {

    @DisplayName("유저 생성시 검증 테스트")
    @Nested
    class validateTest {
        @DisplayName("providerType, providerId 둘 중 하나라도 없으면 에러")
        @Test
        void providerTypeAndProviderId() {
            // given when
            var throwable = catchThrowable(() ->
                new User("email",
                    "name",
                    "password",
                    "profileImageUrl",
                    User.RoleType.USER,
                    null,
                    "testProviderId")
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("providerType, providerId 둘 다 필요합니다.");
        }

        @Test
        void providerType가_없고_password가_없으면_에러() {
            // given when
            var throwable = catchThrowable(() ->
                new User("email",
                    "name",
                    null,
                    "profileImageUrl",
                    User.RoleType.USER,
                    null,
                    null)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password 는 필수입니다.");
        }

        @Test
        void 이메일_형식이_아니면_에러() {
            // given when
            var throwable = catchThrowable(() ->
                new User("invalidEmail",
                    "name",
                    "password",
                    "profileImageUrl",
                    User.RoleType.USER,
                    null,
                    null)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 형식이 올바르지 않습니다.");
        }

        @Test
        void 프로필_이미지_URL_형식이_아니면_에러() {
            // given when
            var throwable = catchThrowable(() ->
                new User("test@test.com",
                    "name",
                    "password",
                    "profileImageUrl",
                    User.RoleType.USER,
                    null,
                    null)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필 이미지 URL 형식이 올바르지 않습니다.");
        }
    }

    @DisplayName("유저 정보 변경시 검증 테스트")
    @Nested
    class updateTest {
        @Test
        void 프로필_이미지_URL_형식이_아니면_에러(){
            // given
            var user = new UserFixture().build();

            // when
            var throwable = catchThrowable(() ->
                user.changeProfileImage("profileImage")
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필 이미지 URL 형식이 올바르지 않습니다.");
        }
    }
}