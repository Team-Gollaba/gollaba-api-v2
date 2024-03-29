package org.tg.gollaba.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.exception.UnAuthorizedException;
import org.tg.gollaba.user.domain.User;

@Component
@Slf4j
public class AuthenticationHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return isUserType(parameter);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !(authentication.getPrincipal() instanceof User user)) {
            throw new UnAuthorizedException();
        }

        return new AuthenticatedUser(
            user.id(),
            user.name(),
            user.email()
        );
    }

    private boolean isUserType(MethodParameter parameter) {
        return isAssignable(parameter, AuthenticatedUser.class);
    }

    private <T> boolean isAssignable(MethodParameter parameter, Class<?> clazz) {
        return clazz.isAssignableFrom(parameter.getParameterType());
    }
}
