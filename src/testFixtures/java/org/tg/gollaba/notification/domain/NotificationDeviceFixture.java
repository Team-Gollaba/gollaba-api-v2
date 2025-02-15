package org.tg.gollaba.notification.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationDeviceFixture implements TestFixture<NotificationDevice> {
    private Long id = 1L;
    private Long userId = 1L;
    private String agentId = "testAgentId";
    private NotificationDevice.OperatingSystemType osType = NotificationDevice.OperatingSystemType.IOS;
    private String deviceName = "testDeviceName";
    private Boolean allowsNotification = true;

    @Override
    public NotificationDevice build(){
        var deviceNotification = new NotificationDevice();
        FixtureReflectionUtils.reflect(deviceNotification, this);
        return deviceNotification;
    }
}
