package org.tg.gollaba.auth.component;

import org.tg.gollaba.auth.vo.IssuedToken;

public interface TokenProvider {

    IssuedToken issue(Long userId);
}
