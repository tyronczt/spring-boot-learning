package com.tyron.springboot.component;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @Description: 自定义error属性
 * @Author: tyron
 * @date: 2019/3/10
 */
@Component
public class MyErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(webRequest, includeStackTrace);
        map.put("author", "tyron");
        // 从request中获取自定义的错误提示
        Object ext = webRequest.getAttribute("ext", 0);
        map.put("ext", ext);
        return map;
    }
}
