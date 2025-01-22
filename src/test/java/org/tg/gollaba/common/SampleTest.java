package org.tg.gollaba.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tg.gollaba.common.client.AppleClient;
import org.tg.gollaba.stats.repository.PollDailyStatsRepository;
import org.tg.gollaba.stats.repository.PollStatsRepository;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
@Slf4j
public class SampleTest {
    @Autowired
    AppleClient appleClient;

    @Test
    void test() {
        var code = "cc839901abba340128c26748ea81ccd1e.0.pruzy.sFn826LNvvGYJ5yWAHfIoA";
        var response = appleClient.getUserInfo(code);
        log.info("response: {}", response);
    }
}
