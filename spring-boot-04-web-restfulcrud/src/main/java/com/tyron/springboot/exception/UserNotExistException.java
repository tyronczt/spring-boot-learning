package com.tyron.springboot.exception;

/**
 * @Description: 用户不存在异常
 * @Author: tyron
 * @date: 2019/3/10
 */
public class UserNotExistException extends RuntimeException {

    public UserNotExistException() {
        super("用户不存在!!!");
    }
}
