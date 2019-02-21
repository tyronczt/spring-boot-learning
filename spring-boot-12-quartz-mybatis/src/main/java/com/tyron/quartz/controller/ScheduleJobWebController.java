package com.tyron.quartz.controller;

import com.tyron.quartz.entity.base.ResponseResult;
import com.tyron.quartz.entity.base.ScheduleEntity;
import com.tyron.quartz.entity.base.TableResult;
import com.tyron.quartz.service.JobService;
import com.tyron.quartz.service.ScheduleJobService;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务调度接口类
 *
 * @author tyron
 * @date 2018-12-14
 */
@RestController
@RequestMapping(value = "/quartz")
public class ScheduleJobWebController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private JobService jobService;

    /**
     * 按条件查询并返回已注册任务列表
     *
     * @param offset 偏移量
     * @param limit  返回数据条数
     * @param key    检索关键字
     * @return
     */
    @GetMapping(value = "/list")
    public TableResult<List<ScheduleEntity>> getAllJobs(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "search") String key) {
        TableResult<List<ScheduleEntity>> result = new TableResult<>();
        result.setTotal(jobService.getJobCount(key));
        result.setRows(jobService.getJobAndTriggerDetails(offset, limit, key));
        return result;
    }

    /**
     * 注册新任务
     *
     * @param scheduleEntity 任务实体类
     * @return
     */
    @PostMapping(value = "/create")
    public ResponseResult create(@RequestBody ScheduleEntity scheduleEntity) {
        ResponseResult result = new ResponseResult();

        // 判断表达式
        if (!CronExpression.isValidExpression(scheduleEntity
                .getCronExpression())) {
            result.setMessage("cron表达式有误，不能被解析");
        }
        try {
            scheduleEntity.setStatus("1");
            scheduleJobService.add(scheduleEntity);
            result.setStatus(200);
            result.setMessage("注册成功");
        } catch (ClassNotFoundException e) {
            result.setStatus(2);
            result.setMessage("找不到指定的类");
        } catch (SchedulerException e) {
            if (e.getMessage().contains(
                    "because one already exists with this identification")) {
                result.setStatus(3);
                result.setMessage("任务组中存在同样的任务名");
            } else {
                result.setStatus(4);
                result.setMessage("未知原因,注册任务失败");
            }
        }

        return result;
    }

    /**
     * 暂停对指定任务的调度，不影响正在执行的该任务实例
     *
     * @param jobName  任务名
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/pause")
    public ResponseResult pause(
            @RequestParam(value = "jobName") String jobName,
            @RequestParam(value = "jobGroup") String jobGroup) throws SchedulerException {
        ResponseResult result = new ResponseResult();
        scheduleJobService.stopJob(jobName, jobGroup);
        result.setStatus(200);
        result.setMessage("暂停成功");
        return result;
    }

    /**
     * 注销任务
     *
     * @param jobName  任务名
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/delete")
    public ResponseResult delete(
            @RequestParam(value = "jobName") String jobName,
            @RequestParam(value = "jobGroup") String jobGroup) throws SchedulerException {
        ResponseResult result = new ResponseResult();
        scheduleJobService.delJob(jobName, jobGroup);
        result.setStatus(200);
        result.setMessage("删除成功");
        return result;
    }


    /**
     * 修改 cron 表达式或任务描述。修改后立即生效
     *
     * @param jobName        任务名
     * @param jobGroup       任务分组
     * @param cronExpression cron表达式
     * @param description    任务描述
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/update")
    public ResponseResult updateCron(
            @RequestParam(value = "jobName") String jobName,
            @RequestParam(value = "jobGroup") String jobGroup,
            @RequestParam(value = "cronExpression") String cronExpression,
            @RequestParam(value = "description") String description) throws SchedulerException {
        ResponseResult result = new ResponseResult();
        // 验证cron表达式
        if (!CronExpression.isValidExpression(cronExpression)) {
            result.setStatus(5);
            result.setMessage("cron表达式有误，不能被解析！");
        } else {
            scheduleJobService.modifyTrigger(jobName, jobGroup, cronExpression);
            jobService.updateJob(jobName, jobGroup, description);
            result.setStatus(200);
            result.setMessage("修改成功");
        }

        return result;
    }

    /**
     * 立即生成一条临时任务，并触发执行，执行完毕后删除临时任务
     *
     * @param jobName  任务名
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/runOnceNow")
    public ResponseResult runOnceNow(
            @RequestParam(value = "jobName") String jobName,
            @RequestParam(value = "jobGroup") String jobGroup) throws SchedulerException {
        ResponseResult result = new ResponseResult();
        scheduleJobService.startNowJob(jobName, jobGroup);
        result.setStatus(200);
        result.setMessage("触发成功");
        return result;
    }

    /**
     * 恢复
     *
     * @throws SchedulerException
     */
    @GetMapping(value = "/resume")
    public ResponseResult resume(
            @RequestParam(value = "jobName") String jobName,
            @RequestParam(value = "jobGroup") String jobGroup) throws SchedulerException {
        ResponseResult result = new ResponseResult();
        scheduleJobService.restartJob(jobName, jobGroup);
        result.setStatus(200);
        result.setMessage("恢复成功");
        return result;
    }

}
