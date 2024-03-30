package org.tg.gollaba.common;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tg.gollaba.common.exception.ErrorNotificationSender;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
public class SampleTest {
    @Autowired
    ErrorNotificationSender errorNotificationSender;

    @Test
    void test() {
        errorNotificationSender.send("test", new RuntimeException("test"));
    }
}
