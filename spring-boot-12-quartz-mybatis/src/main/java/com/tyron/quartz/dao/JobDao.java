package com.tyron.quartz.dao;

import com.tyron.quartz.entity.base.ScheduleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * quartz任务数据库接口
 */
public interface JobDao {

    /**
     * 获取列表详情 [limit 3, 7; // 返回4-11行]
     *
     * @param offset 偏移量
     * @param limit  数目
     * @param key    job的name
     * @return
     */
    List<ScheduleEntity> getJobAndTriggerDetails(@Param("offset") Integer offset, @Param("limit") Integer limit, @Param("key") String key);

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
    void updateJob(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup, @Param("description") String description);

}
