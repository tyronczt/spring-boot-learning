package com.tyron.springboot.config;

import com.tyron.springboot.filter.MyFilter;
import com.tyron.springboot.listener.MyListener;
import com.tyron.springboot.servlet.MyServlet;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @Description: 服务器配置
 * @Author: tyron
 * @date: 2019/3/15
 */
@Configuration
public class MyServerConfig {

    // 注册Servlet组件
    @Bean
    public ServletRegistrationBean myServlet() {
        return new ServletRegistrationBean(new MyServlet(), "/myservlet");
    }

    // 注册Filter组件
    @Bean
    public FilterRegistrationBean myFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyFilter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/myfilter", "/myservlet"));
        return filterRegistrationBean;
    }

    // 注册Listener
    @Bean
    public ServletListenerRegistrationBean myListener(){
        return  new ServletListenerRegistrationBean(new MyListener());
    }

    //    SpringBoot1.5.x
//    @Bean
//    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
//        return new EmbeddedServletContainerCustomizer() {
//            @Override
//            public void customize(ConfigurableEmbeddedServletContainer container) {
//                container.setPort(8090);
//            }
//        };
//    }
    // SpringBoot2.x.x
    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            // 定制嵌入式的Servlet容器相关规则
            @Override
            public void customize(ConfigurableServletWebServerFactory factory) {
                factory.setPort(8090);
            }
        };
    }
    // 附上官方文档：https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-programmatic-embedded-container-customization
}
