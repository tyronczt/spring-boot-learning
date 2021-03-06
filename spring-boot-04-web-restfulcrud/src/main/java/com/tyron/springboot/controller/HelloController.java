package com.tyron.springboot.controller;

import com.tyron.springboot.exception.UserNotExistException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("exception")
    @ResponseBody
    public String exception(@RequestParam("msg") String msg) {
        if (msg.equals("aaa")) {
            throw new UserNotExistException();
        }
        return "exception";
    }

}
