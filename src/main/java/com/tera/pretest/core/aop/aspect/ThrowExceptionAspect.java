package com.tera.pretest.core.aop.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
public class ThrowExceptionAspect {

    @Around("execution(* com..service.*Service.*(..))")
    public Object handleDatabaseException(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String path = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String method = proceedingJoinPoint.getSignature().getName();
        Object[] methodArgs = proceedingJoinPoint.getArgs();
        try {
            return proceedingJoinPoint.proceed();
        } catch (RuntimeException ex) {
            log.error("Start Root: {}, Method: {}, with arguments: {}, Error: {} "
                    , path, method, methodArgs,  ex);
            throw ex;
        }
    }

}
