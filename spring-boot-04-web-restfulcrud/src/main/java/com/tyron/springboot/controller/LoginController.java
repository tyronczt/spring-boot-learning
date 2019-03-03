package com.tyron.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Description: 登录控制器
 * @Author: tyron
 * @date: 2019/3/3
 */
@Controller
public class LoginController {

    @PostMapping("/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Map<String, Object> map, HttpSession session) {
        if (!StringUtils.isEmpty(username) && "123456".equals(password)) {
            // 设值session
            session.setAttribute("loginUser", username);
            // 登录成功，重定向
            return "redirect:/main.html";
        } else {
            map.put("errMsg", "用户名或密码错误");
            return "login";
        }
    }
}
