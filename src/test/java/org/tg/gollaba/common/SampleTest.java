package org.tg.gollaba.common;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tg.gollaba.stats.repository.PollDailyStatsRepository;
import org.tg.gollaba.stats.repository.PollStatsRepository;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
public class SampleTest {
    @Autowired
    PollStatsRepository pollStatsRepository;
    @Autowired
    PollDailyStatsRepository pollDailyStatsRepository;

    @Test
    void test() {
    }
}
