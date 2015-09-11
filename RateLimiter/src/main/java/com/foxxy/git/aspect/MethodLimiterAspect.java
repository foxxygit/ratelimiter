package com.foxxy.git.aspect;

import java.util.concurrent.RejectedExecutionException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.foxxy.git.LimitStrategy;
import com.foxxy.git.Limiter;
import com.foxxy.git.annotation.MethodLimiter;
import com.foxxy.git.factory.RateLimiterFactory;
import com.foxxy.git.spring.SpringContext;

@Aspect
@Component
public class MethodLimiterAspect {

    @Around("@annotation(annotation)")
    public Object advice(ProceedingJoinPoint joinPoint, MethodLimiter annotation) throws Throwable {
        String name = annotation.name();
        Limiter rateLimiter = RateLimiterFactory.getInstance().getRateLimiter(name);
        if (null == rateLimiter) {
            throw new IllegalArgumentException("Limiter not exsit name:" + name);
        }
        String beanName = annotation.beanName();
        LimitStrategy limitStrategy = SpringContext.getBean(beanName, LimitStrategy.class);
        try {
            if (!rateLimiter.tryAcquire(limitStrategy)) {
                throw new BusyException("server is busy");
            }
            rateLimiter.increment();
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            if (e instanceof RejectedExecutionException) {
                throw new BusyException("server is busy");
            }
            throw e;
        }
    }
}
