package com.foxxy.git;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.foxxy.git.service.HelloService;
import com.foxxy.git.zookeeper.ZKBootStartup;

public class LimiterTest {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(5);

        ZKBootStartup starter = new ZKBootStartup();
        // 启动zk
        starter.start();

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext.xml");

        final HelloService helloService = (HelloService) context.getBean("helloService");

        // 启动后睡2分钟
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            // donothing
        }

        for (int i = 0; i < 100; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        helloService.sayHello();
                    } catch (Exception e) {
                        System.out.println("busy busy busy!!!!!!!!!!!");
                    }
                }
            });
        }
        // 大大的睡一会
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            // donothing
        }
    }
}
