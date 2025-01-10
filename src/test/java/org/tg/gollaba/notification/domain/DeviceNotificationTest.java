package org.tg.gollaba.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class DeviceNotificationTest {
    @DisplayName("디바이스 등록 검증 테스트")
    @Nested
    class validateTest{
        @Test
        void userId가_null이면_에러(){
            // given when
            var throwable = catchThrowable(() ->
                new DeviceNotification(
                    null,
                    "agentId",
                    DeviceNotification.OperatingSystemType.IOS,
                    "deviceName",
                    true)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId는 null일 수 없습니다. currentValue: %d", null);
        }

        @Test
        void osType이_null이면_에러(){
            // given when
            var throwable = catchThrowable(() ->
                new DeviceNotification(
                    1L,
                    "agentId",
                    null,
                    "deviceName",
                    true)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("osType은 필수입니다.");
        }

        @Test
        void deviceName이_null이면_에러() {
            // given when
            var throwable = catchThrowable(() ->
                new DeviceNotification(
                    1L,
                    "agentId",
                    DeviceNotification.OperatingSystemType.IOS,
                    null,
                    true)
            );

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deviceName은 비거나 공백일 수 없습니다. currentValue: %s".formatted(null));
        }
    }
}
