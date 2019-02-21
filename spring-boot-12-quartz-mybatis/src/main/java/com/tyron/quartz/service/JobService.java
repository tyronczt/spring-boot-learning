package com.tyron.quartz.service;

import com.tyron.quartz.entity.base.ScheduleEntity;

import java.util.List;

/**
 * 任务服务接口
 */
public interface JobService {

    /**
     * 获取列表详情 [limit 3, 7; // 返回4-11行]
     *
     * @param offset 偏移量
     * @param limit  数目
     * @param key    job的name
     * @return
     */
    List<ScheduleEntity> getJobAndTriggerDetails(Integer offset, Integer limit, String key);

    /**
     * 获取job数量
     *
     * @param key 搜索关键值
     * @return
     */
    Integer getJobCount(String key);

    /**
     * 更新job
     *
     * @param jobName     任务名
     * @param jobGroup    任务组
     * @param description 任务描述
     */
    void updateJob(String jobName, String jobGroup, String description);

}
