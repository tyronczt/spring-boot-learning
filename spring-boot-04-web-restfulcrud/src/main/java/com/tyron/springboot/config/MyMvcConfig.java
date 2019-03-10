package com.tyron.springboot.config;

import com.tyron.springboot.component.LoginHandlerInterceptor;
import com.tyron.springboot.component.MyLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Description: MVC扩展配置
 * @Author: tyron
 * @date: 2019/3/3
 */
//@EnableWebMvc
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送 /tyron 请求，来到hello页面
        registry.addViewController("/tyron").setViewName("hello");
        // 设置浏览器默认页面为login.html
        registry.addViewController("/").setViewName("login");
        registry.addViewController("index.html").setViewName("login");
        // 设置登录后跳转的主页
        registry.addViewController("main.html").setViewName("dashboard");
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/index.html","/","/user/login",
//                "/asserts/**","/webjars/**");
//    }

    //    //所有的WebMvcConfigurerAdapter组件都会一起起作用
//    @Bean //将组件注册在容器
//    public WebMvcConfigurerAdapter webMvcConfigurerAdapter(){
//        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
//            @Override
//            public void addViewControllers(ViewControllerRegistry registry) {
//                registry.addViewController("/").setViewName("login");
//                registry.addViewController("/index.html").setViewName("login");
//            }
//        };
//        return adapter;
//    }

    // 注册自己的LocaleResolver
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }
}
