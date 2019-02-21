package com.tyron.quartz.service;

import com.tyron.quartz.entity.base.ScheduleEntity;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @Desription 定时任务服务接口
 * @Modify: tyron
 * @Date: Created in 2019-2-20
 */
public interface ScheduleJobService {
    /**
     * 添加定时任务
     *
     * @param scheduleEntity
     */
     void add(ScheduleEntity scheduleEntity) throws ClassNotFoundException,
            SchedulerException;

    /**
     * 获取所有JobDetail
     *
     * @return 结果集合
     */
     List<JobDetail> getJobs();

    /**
     * 获取所有计划中的任务
     *
     * @return 结果集合
     */
     List<ScheduleEntity> getAllScheduleJob();

    /**
     * 获取所有运行中的任务
     *
     * @return 结果集合
     */
     List<ScheduleEntity> getAllRuningScheduleJob();

    /**
     * 获取所有的触发器
     *
     * @return 结果集合
     */
     List<ScheduleEntity> getTriggersInfo();

    /**
     * 暂停任务
     *
     * @param name  任务名
     * @param group 任务组
     */
     void stopJob(String name, String group) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param name  任务名
     * @param group 任务组
     */
     void restartJob(String name, String group) throws SchedulerException;

    /**
     * 立马执行一次任务
     *
     * @param name  任务名
     * @param group 任务组
     */
     void startNowJob(String name, String group)
            throws SchedulerException;

    /**
     * 删除任务
     *
     * @param name  任务名
     * @param group 任务组
     */
     void delJob(String name, String group) throws SchedulerException;

    /**
     * 修改触发器时间
     *
     * @param name  任务名
     * @param group 任务组
     * @param cron  cron表达式
     */
     void modifyTrigger(String name, String group, String cron) throws SchedulerException;

    /**
     * 暂停调度器
     */
     void stopScheduler() throws SchedulerException;
}
