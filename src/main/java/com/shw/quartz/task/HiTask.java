package com.shw.quartz.task;

import com.shw.quartz.util.ScheduleConstants;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/17 14:27
 * @description
 */
public class HiTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String format = new SimpleDateFormat("yyyy-dd-mm HH:mm:ss").format(new Date());
        System.out.println("Hi.....逻辑执行时间："+format);
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("Hi...."+jobDataMap.get(ScheduleConstants.TASK_PROPERTIES));
        System.out.println("Hi.....真正逻辑执行的代码。。。");
    }
}
