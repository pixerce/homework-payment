package com.example.mission.payment.sync;

import com.example.mission.payment.exception.ErrorCode;
import com.example.mission.payment.exception.InvalidStateRequestException;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DisallowConcurrentRequestHandler {

    Map keyStore = Collections.synchronizedMap(new PassiveExpiringMap(2, TimeUnit.SECONDS));

    @Around("@annotation(disallowConcurrentRequest)")
    public Object around(ProceedingJoinPoint pjp, DisallowConcurrentRequest disallowConcurrentRequest) throws Throwable {

        Parameter [] parameters = ((MethodSignature)pjp.getSignature()).getMethod().getParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return pjp.proceed();
        } else {
            Object [] args = pjp.getArgs();
            HashCodeBuilder builder = new HashCodeBuilder();
            for (int i = 0; i < parameters.length; i++) {
                Annotation annotation = parameters[i].getAnnotation(HashingTarget.class);
                if (annotation != null) {
                    builder.append(args[i]);
                }
            }

            synchronized (keyStore) {
                int hash = builder.toHashCode();
                if (keyStore.containsKey(hash)) {
                    throw new InvalidStateRequestException(ErrorCode.DuplicateRequest);
                }
                keyStore.put(hash, new Object());
            }
            return pjp.proceed();
        }
    }
}
