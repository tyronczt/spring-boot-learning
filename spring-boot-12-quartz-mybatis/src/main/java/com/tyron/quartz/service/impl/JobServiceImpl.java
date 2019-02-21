package com.tyron.quartz.service.impl;

import com.tyron.quartz.dao.JobDao;
import com.tyron.quartz.entity.base.ScheduleEntity;
import com.tyron.quartz.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 任务服务接口实现
 * @Author: tyron
 * @date: 2019/2/21
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired(required = false)
    private JobDao jobDao;

    @Override
    public List<ScheduleEntity> getJobAndTriggerDetails(Integer offset, Integer limit, String key) {
        return jobDao.getJobAndTriggerDetails(offset, limit, key);
    }

    @Override
    public Integer getJobCount(String key) {
        return jobDao.getJobCount(key);
    }

    @Override
    @Transactional
    public void updateJob(String jobName, String jobGroup, String description) {
        jobDao.updateJob(jobName, jobGroup, description);
    }
}
