package com.foxxy.git.strategy;

import java.util.concurrent.RejectedExecutionException;

import com.foxxy.git.LimitStrategy;

public class RejectTaskLimitStrategy implements LimitStrategy{

    @Override
    public <T> void execute(T... t) {
        throw new RejectedExecutionException();        
    }
}
