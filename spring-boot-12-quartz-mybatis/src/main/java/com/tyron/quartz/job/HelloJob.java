package com.tyron.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;


/**
 * 任务类示例 如果该任务一次不允许出现多个实例并行执行，则加 @DisallowConcurrentExecution 注解
 * 该注解将避免某任务当前还在执行过程中，应定时时间到而被再次调度
 * 不要将 job 类注册为Bean，job的生命周期由 walle-quartz 框架自动管理
 * 但是 job 类中仍然支持 @Autowired 和 @value 注解注入依赖。已在 walle-quartz框架中做了处理，当job启动时会自动注入依赖
 *
 * @author tyron
 */

@DisallowConcurrentExecution
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Hello Job执行时间: " + new Date());
    }
}
