package org.tg.gollaba.common.support;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class ExceptionUtils {
    private ExceptionUtils() {
    }

    public static void ignoreException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("[ExceptionUtils.ignoreException] message: {}", e.getMessage(), e);
            // ignore
        }
    }
}
