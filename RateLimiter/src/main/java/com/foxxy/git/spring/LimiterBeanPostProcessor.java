package com.foxxy.git.spring;

import java.lang.reflect.Method;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.foxxy.git.RateLimiter;
import com.foxxy.git.annotation.MethodLimiter;
import com.foxxy.git.factory.RateLimiterFactory;
import com.foxxy.git.listener.LimiterRateMonitorListener;
import com.foxxy.git.zookeeper.client.ZKClusterClient;
import com.foxxy.git.zookeeper.factory.ZookeeperClientMakerFactory;

@Service
public class LimiterBeanPostProcessor implements BeanPostProcessor {

    private final static String PATH_PREFIX = "/rate";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods=AopUtils.getTargetClass(bean).getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MethodLimiter.class)) {
                MethodLimiter methodLimiter = method.getAnnotation(MethodLimiter.class);
                // 获取zkclient
                ZKClusterClient client = ZookeeperClientMakerFactory.getInstance().getZKClusterClient("zkClient");
                String name = methodLimiter.name();
                int rate = methodLimiter.rate();
                // 创建rateLimiter
                RateLimiterFactory.getInstance().putIfAbsent(name, new RateLimiter().create(name, rate));
                String path = PATH_PREFIX + "/" + name;
                if (!client.isExsit(path)) {
                    // 创建节点
                    client.createNode(path, String.valueOf(rate), com.foxxy.git.zookeeper.CreateMode.PERSISTENT);
                }
                client.registerConnectionListener(new LimiterRateMonitorListener(path));
                
                client.registerNodeListener(path, new LimiterRateMonitorListener(path));
            }

        }
        return bean;
    }
}
