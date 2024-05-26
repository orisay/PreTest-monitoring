package com.tera.pretest.core.aop.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

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
        } catch (NullPointerException | ExecutionException asyncException) {
            String errorMessage = Arrays.toString(asyncException.getStackTrace());
            log.error("Start Root: {}, Method: {}, with arguments: {}, Error: {} "
                    , path, method, methodArgs, errorMessage, asyncException);
            throw asyncException;
        } catch (Throwable exception) {
            String errorMessage = Arrays.toString(exception.getStackTrace());
            log.error("Start Root: {}, Method: {}, with arguments: {}, Error: {} "
                    , path, method, methodArgs, errorMessage, exception);
            throw exception;
        }
    }

}
