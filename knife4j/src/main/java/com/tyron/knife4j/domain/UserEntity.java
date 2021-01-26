package com.tyron.knife4j.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户实体
 * @Author: tyron
 * @Date: Created in 2021/1/25
 */
@Data
public class UserEntity {

    @ApiModelProperty(value="姓名",example="张飞")
    private String name;

    @ApiModelProperty(value="年龄",example="18")
    private Integer age;

    @ApiModelProperty(value="生日",example="2000-01-01")
    private Date brithday;
}
