package com.shw.quartz.task;
import java.text.SimpleDateFormat;
import	java.util.Date;

import com.shw.quartz.util.ScheduleConstants;
import org.quartz.*;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/17 14:27
 * @description
 * DisallowConcurrentExecution 不允许并发执行，默认是允许并发执行
 * 虽然这个注解放在HelloTask类上，但不针对这个类，也就是说这个类是可以允许被并发执行的
 * 这个注解是针对jobDetail实例的，是说明不能并发执行同一个jobDetail实例
 * 比如：当有个HelloDetail实例绑定了这个HelloTask,是不允许同时运行多个HelloDetail实例的
 * 需要等上一个实例运行完毕才能继续下一个。
 * 但是当上一个运行实例时间过长，而两个实例之间运行的间隔又小于上个实例运行的时间，就会产生第二个
 * 实例不在预定的时间执行。
 */
@DisallowConcurrentExecution
public class HelloTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String format = new SimpleDateFormat("yyyy-dd-mm HH:mm:ss").format(new Date());
        System.out.println("Hello......逻辑执行时间："+format);
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("Hello......"+jobDataMap.get(ScheduleConstants.TASK_PROPERTIES));
        System.out.println("Hello......真正逻辑执行的代码。。。");
    }
}
