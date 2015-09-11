package com.foxxy.git.strategy;

import com.foxxy.git.LimitStrategy;

public class WaitLimitStrategy implements LimitStrategy {
    
    private final Object obj=new Object();
    
    @Override
    public <T> void execute(T... t) {
        try {
            obj.wait((Long)t[0]);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
}
