package com.tyron.springboot.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Description: 自定义配置Filter
 * @Author: tyron
 * @date: 2019/3/15
 */
public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter process......");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
