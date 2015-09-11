package com.foxxy.git.service;

import org.springframework.stereotype.Service;

import com.foxxy.git.annotation.MethodLimiter;

@Service("helloService")
public class HelloServiceImpl implements HelloService {

    @MethodLimiter(name = "HelloService.sayHello", rate = 5)
    public void sayHello() {
        System.out.println("say hello!!!");
    }

}
