package org.tg.gollaba.auth.component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
public class CookieHandler {

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        var cookies = request.getCookies();

        if (cookies == null || cookies.length < 1) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(name))
            .findAny();
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        var cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public void addSecuredCookie(HttpServletResponse response, String name, String value, int maxAge) {
        var cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        var cookies = request.getCookies();

        if (cookies == null || cookies.length < 1) return;

        Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(name))
            .forEach(cookie -> {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            });
    }

    public String serialize(Object object) {
        return Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(object));
    }

    public <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
            SerializationUtils.deserialize(
                Base64.getUrlDecoder()
                    .decode(cookie.getValue())
            )
        );
    }
}
