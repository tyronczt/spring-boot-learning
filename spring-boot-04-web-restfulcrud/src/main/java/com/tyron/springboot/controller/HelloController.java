package com.tyron.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description: 控制器
 * @Author: tyron
 * @date: 2019/3/1
 */
@Controller
public class HelloController {

    @RequestMapping("hello")
    public String hello(Map<String, Object> map) {
        map.put("name", "<h1>tyron</h1>");
        map.put("names", Arrays.asList("tyron0", "tyron1", "tyron2"));
        return "hello";
    }

}
