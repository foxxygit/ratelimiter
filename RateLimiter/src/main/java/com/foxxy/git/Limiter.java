package com.foxxy.git;

public interface Limiter {
    
    Limiter create(String rateLimiterName,int permitsPerSecond);
    
    boolean tryAcquire(LimitStrategy strategy);
    
    boolean tryAcquire(long millsenconds,LimitStrategy strategy);
    
    void increment();
    
    int get();
    
    void rest();
    
    void setRate(int permitsPerSecond);
}
