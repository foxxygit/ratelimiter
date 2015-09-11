package com.foxxy.git;

/**
 * 策略，当达到流控阀值时需要执行的策略，是等待还是拒绝
 */
public interface LimitStrategy {
    
    <T> void execute(T...t);
}
