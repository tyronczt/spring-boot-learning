package com.tyron.springboot.config;

import com.tyron.springboot.service.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 配置类
 * @Author: tyron
 * @date: 2019/1/29
 *
 * @Configuration 指明当前类是配置类，代替之前的xml配置文件
 * 在Spring配置文件中使用<bean></bean>，在配置文件中使用@Bean 注解
 */
@Configuration
public class MyAppConfig {

    /**
     * @Bean 注解的作用是将方法的返回值添加到容器中；
     * 容器中该组件默认的id就是方法名
     */
    @Bean
    public HelloService helloService02() {
        System.out.println("通过配置类的方式注入Bean");
        return new HelloService();
    }
}
