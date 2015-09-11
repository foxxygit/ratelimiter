package com.foxxy.git.zookeeper.exception;

public class ZKOperateException extends RuntimeException {

    /**
     */
    private static final long serialVersionUID = -500941966298337985L;

    public ZKOperateException(String message) {
        super(message);
    }

    public ZKOperateException(String message, Throwable cause) {
        super(message, cause);
    }
}
