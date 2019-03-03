package com.tyron.springboot.component;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @Description: 区域信息解析器
 * @Author: tyron
 * @date: 2019/3/3
 */
public class MyLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String parameter = request.getParameter("l");
        // 初始化区域信息，默认系统区域
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(parameter)) {
            String[] strings = parameter.split("_");
            locale = new Locale(strings[0], strings[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
