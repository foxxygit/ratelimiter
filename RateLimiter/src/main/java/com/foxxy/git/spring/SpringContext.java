package com.foxxy.git.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * spring上下文获取
 *
 */
@Service
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (BeansException e) {
            throw new NoSuchBeanDefinitionException(beanName, "no beanName define");
        }
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        try {
            return applicationContext.getBean(beanName, clazz);
        } catch (BeansException e) {
            throw new NoSuchBeanDefinitionException(beanName, "no beanName define");
        }
    }

    public static <T> List<T> getBeansOfType(Class<T> clazzType) {
        try {
            return new ArrayList<T>(applicationContext.getBeansOfType(clazzType).values());
        } catch (BeansException e) {
            throw new BeanNotOfRequiredTypeException("", clazzType, clazzType);
        }
    }

}
