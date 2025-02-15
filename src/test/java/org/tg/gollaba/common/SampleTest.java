package org.tg.gollaba.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tg.gollaba.notification.service.ClosePollService;

import java.time.LocalDateTime;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
@Slf4j
public class SampleTest {
    @Autowired
    ClosePollService service;

    @Test
    void test() {
        // 2025-02-14 15:34:44.000000
        var from = LocalDateTime.of(2025, 2, 14, 15, 34, 44);
        var to = LocalDateTime.of(2025, 2, 14, 15, 34, 44);

        service.close(from, to);
    }
}
