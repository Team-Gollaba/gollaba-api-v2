package org.tg.gollaba.poll.controller.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.tg.gollaba.poll.component.HashIdHandler;

@Aspect
@Component
@RequiredArgsConstructor
public class HashIdAspect {

    private final HashIdHandler hashIdHandler;

    @Around("@annotation(useHashId)")
    public Object parseHashId(ProceedingJoinPoint pjp, UseHashId useHashId) throws Throwable {
        Object[] args = pjp.getArgs();

        args[0] =  hashIdHandler.decode(args[0].toString());

        return pjp.proceed(args);
    }

}
