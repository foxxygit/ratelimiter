package com.foxxy.git.factory;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.util.Assert;

import com.foxxy.git.Limiter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class RateLimiterFactory {

    private static RateLimiterFactory factory = new RateLimiterFactory();

    private static ConcurrentMap<String, Limiter> limiterMap = Maps.newConcurrentMap();

    private static AtomicBoolean inited = new AtomicBoolean(false);

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    private RateLimiterFactory() {
    }

    public static RateLimiterFactory getInstance() {
        // 启动线程
        if (!inited.compareAndSet(true, true)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    restRateLimiter();
                }

            }).start();
        }
        return factory;
    }

    public void putIfAbsent(String key, Limiter value) {
        Assert.notNull(key);
        Assert.notNull(value);
        limiterMap.putIfAbsent(key, value);
    }

    public Limiter getRateLimiter(String key) {
        return limiterMap.get(key);
    }

    private static List<Limiter> values() {
        return Lists.newArrayList(limiterMap.values());
    }

    /**
     * 1s钟将速率控制器重置为0
     */
    private static void restRateLimiter() {
        final List<Limiter> rateLimiters = values();
        for (final Limiter rateLimiter : rateLimiters) {
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    rateLimiter.rest();
                }
            }, 120, 1, TimeUnit.SECONDS);// 首次启动2分钟后开始执行，1s钟执行一次
        }
    }

}
