package org.tg.gollaba.notification.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

import static org.tg.gollaba.notification.domain.AppNotificationHistory.Type.POLL_TERMINATE;

@Getter
@Setter
@Accessors(chain = true)
public class AppNotificationHistoryTestFixture implements TestFixture<AppNotificationHistory> {
    private Long id = 1L;
    private AppNotificationHistory.Type type = POLL_TERMINATE;
    private AppNotificationHistory.Status status = null;
    private Long userId = 1L;
    private String agentId = "agentId";
    private long deviceNotificationId = 1L;
    private String title = "title";
    private String content = "content";
    private String failReason = null;

    @Override
    public AppNotificationHistory build(){
        var appNotificationHistory = new AppNotificationHistory();
        FixtureReflectionUtils.reflect(appNotificationHistory, this);
        return appNotificationHistory;
    }
}
