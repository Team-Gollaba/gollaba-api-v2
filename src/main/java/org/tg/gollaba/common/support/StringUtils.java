package org.tg.gollaba.common.support;

import org.apache.commons.lang3.RandomStringUtils;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isBlank();
    }

    public static boolean hasText(String str) {
        return !isBlank(str);
    }
}
