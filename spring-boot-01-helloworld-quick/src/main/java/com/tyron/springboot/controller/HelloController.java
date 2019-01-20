package com.tyron.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: hello控制类
 * @Author: tyron
 * @date: 2019/1/20
 */
//@Controller
//@ResponseBody
@RestController
public class HelloController {

    @RequestMapping("hello")
    public String hello() {
        return "hello world quick!";
    }
}
