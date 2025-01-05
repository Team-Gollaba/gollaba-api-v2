package org.tg.gollaba.stats.repository;

import java.util.List;
import java.util.Map;

public interface PollSearchStatRepositoryCustom {
    List<Map<String, Object>> findTop10SearchedWords();
}
