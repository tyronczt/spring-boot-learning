package com.tyron.springboot.component;

import com.tyron.springboot.exception.UserNotExistException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 自定义异常处理器
 * @Author: tyron
 * @date: 2019/3/10
 */
@ControllerAdvice
public class MyExceptionHandler {

    /**
     * 1、浏览器和其他客户端访问都是json数据
     */
//    @ResponseBody
//    @ExceptionHandler(UserNotExistException.class)
//    public Map<String, Object> handException(Exception e) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("code","user not exist");
//        map.put("msg",e.getMessage());
//        return  map;
//    }
    @ExceptionHandler(UserNotExistException.class)
    public String handException(Exception e, HttpServletRequest request) {
        // AbstractErrorController：getStatus方法：
        // Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        request.setAttribute("javax.servlet.error.status_code", 400);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "user not exist");
        map.put("msg", e.getMessage());
        // 将错误提示设值到request中
        request.setAttribute("ext", map);
        // 转发到默认的BasicErrorController /error请求中
        return "forward:/error";
    }
}
