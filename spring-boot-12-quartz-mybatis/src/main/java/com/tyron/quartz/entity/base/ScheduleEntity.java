package com.tyron.quartz.entity.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 定时任务实体
 */
@Data
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名
     */
    private String jobName;

    /**
     * 任务组
     */
    private String jobGroup;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 执行任务的类(完整路径  包含包名)
     */
    private String className;

    /**
     * 首次执行时间戳
     */
    private Long startTime;

    /**
     * 上次执行时间
     */
    private Long prevTime;

    /**
     * 下次执行时间
     */
    private Long nextTime;

}