package com.tyron.quartz.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * 任务工厂，覆盖原生实现，解决动态生成 job 时无法注入bean的问题
 *
 * @author tyron
 * @date 2018-11-27
 */
@Component("jobFactory")
public class JobFactory extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //覆盖了super的createJobInstance方法，对其创建出来的类再进行autowire
        Object jobInstance = super.createJobInstance(bundle);
        beanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}  
