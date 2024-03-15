package org.tg.gollaba.auth.component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface CookieHandler {

    Optional<Cookie> getCookie(HttpServletRequest request, String name);

    void addCookie(HttpServletResponse response, String name, String value, int maxAge);

    void addSecuredCookie(HttpServletResponse response, String name, String value, int maxAge);

    void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name);

    String serialize(Object object);

    <T> T deserialize(Cookie cookie, Class<T> cls);
}
