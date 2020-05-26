package com.example.mission.payment.annotation;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class BlockDuplicationComponent {

    @Around("@annotation(BlockDuplication)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        System.err.println((String)pjp.getArgs()[0]);
        for (Parameter parameter : method.getParameters()) {
            parameter.getAnnotation(CacheKey.class);


        }

        Object [] args = pjp.getArgs();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (parameter.getAnnotation(CacheKey.class) != null) {

            }
        }
        System.err.println(method.getParameters());
        Object result = pjp.proceed();
        return result;
    }
}
