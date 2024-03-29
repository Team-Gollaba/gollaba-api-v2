package org.tg.gollaba.auth.vo;

public record AuthenticatedUser(
    Long id,
    String name,
    String email
) {
}
