package org.tg.gollaba.notification.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class DeviceNotificationFixture implements TestFixture<DeviceNotification> {
    private Long id = 1L;
    private Long userId = 1L;
    private String agentId = "testAgentId";
    private DeviceNotification.OperatingSystemType osType = DeviceNotification.OperatingSystemType.IOS;
    private String deviceName = "testDeviceName";
    private Boolean allowsNotification = true;

    @Override
    public DeviceNotification build(){
        var deviceNotification = new DeviceNotification();
        FixtureReflectionUtils.reflect(deviceNotification, this);
        return deviceNotification;
    }
}
