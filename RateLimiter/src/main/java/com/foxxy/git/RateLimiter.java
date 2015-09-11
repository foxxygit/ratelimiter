package com.foxxy.git;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.factory.RateLimiterFactory;

public class RateLimiter implements Limiter {

    private final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private AtomicInteger counter;

    private int permitsPerSecond;

    private String rateLimiterName;

    @Override
    public synchronized Limiter create(String rateLimiterName, int permitsPerSecond) {
        if (null == counter) {
            counter = new AtomicInteger(0);
        }
        if (null != RateLimiterFactory.getInstance().getRateLimiter(rateLimiterName)) {
            throw new IllegalArgumentException("rateLimiterName is Illegal:" + rateLimiterName);
        }
        this.permitsPerSecond = permitsPerSecond;
        this.rateLimiterName = rateLimiterName;
        RateLimiterFactory.getInstance().putIfAbsent(rateLimiterName, this);
        return this;
    }

    @Override
    public boolean tryAcquire(LimitStrategy strategy) {
        // 如果大于限制
        if (this.get() > permitsPerSecond) {
            strategy.execute();
        }
        return true;
    }

    @Override
    public boolean tryAcquire(long millsenconds, LimitStrategy strategy) {
        // 如果大于限制
        if (this.get() > permitsPerSecond) {
            strategy.execute(millsenconds);
        }
        return true;
    }

    @Override
    public void increment() {
        if (null == counter) {
            throw new RuntimeException("counter not init");
        }
        counter.incrementAndGet();
    }

    @Override
    public int get() {
        if (null == counter) {
            throw new RuntimeException("counter not init");
        }
        return counter.get();
    }

    @Override
    public synchronized void setRate(int permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public void rest() {
        if (null == counter) {
            throw new RuntimeException("counter not init");
        }
        // 在重置时打印当前1s钟的tps，基于日志做实时tps统计
        log.info("The api {} tps is {} every senconds", rateLimiterName, counter.get());
        counter.set(0);
    }
}
