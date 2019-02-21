package com.tyron.quartz.entity.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果处理
 */
@Data
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private int status;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 具体数据
     */
    private T data;

    public ResponseResult() {
        this(200, "success");
    }

    public ResponseResult(T data) {
        this();
        this.data = data;
    }

    public ResponseResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseResult(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

} 