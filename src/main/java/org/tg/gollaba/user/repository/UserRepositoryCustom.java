package org.tg.gollaba.user.repository;

import java.util.List;
import java.util.Map;

public interface UserRepositoryCustom {
    Map<Long, String> findProfileImagesByUserIds(List<Long> userIds);
}
