package com.tyron.knife4j.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.tyron.knife4j.domain.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: test
 * @Author: tyron
 * @Date: Created in 2021/1/25
 */
@Api(tags = "首页模块")
@RestController
public class Knife4jController {

    @ApiOperationSupport(author = "tyron")
    @ApiImplicitParam(name = "name", value = "姓名", required = true)
    @ApiOperation(value = "向客人问好")
    @GetMapping("/sayHi")
    public ResponseEntity<String> sayHi(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok("Hi:" + name);
    }

    @ApiOperationSupport(author = "tyron")
    @ApiOperation(value = "添加人员")
    @PostMapping("/addPerson")
    public ResponseEntity<String> addPerson(@RequestBody UserEntity userEntity) {
        return ResponseEntity.ok("Hi:" + userEntity);
    }
}
