package org.tg.gollaba.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tg.gollaba.auth.component.JwtTokenHandler;
import org.tg.gollaba.user.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenHandler jwtTokenHandler;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        var jwt = extractToken(request);

        if (StringUtils.hasText(jwt) && jwtTokenHandler.isValidToken(jwt)) {
            var userId = jwtTokenHandler.parseToken(jwt).get("uid", Long.class);
            Authentication authentication = generateAuthentication(request, userId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Authentication generateAuthentication(HttpServletRequest request, Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var authentication = new UsernamePasswordAuthenticationToken(user, user.password(), List.of(() -> user.roleType().name()));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return bearerToken.substring(7);
    }
}
