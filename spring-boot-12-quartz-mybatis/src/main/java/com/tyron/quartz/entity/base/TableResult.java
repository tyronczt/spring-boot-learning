package com.tyron.quartz.entity.base;

import lombok.Data;

/**
 * 列表检索结果
 */
@Data
public class TableResult<T> {

    /**
     * 总量
     */
    private int total;

    /**
     * 检索结果列表
     */
    private T rows;

}
