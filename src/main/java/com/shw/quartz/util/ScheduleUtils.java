package com.shw.quartz.util;

import com.shw.quartz.domain.SysJob;
import org.quartz.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 16:06
 * @description 定时任务工具类
 */
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static void getQuartzJobClass(SysJob sysJob) {
        // 从前端传来是否并发执行，做判断
        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
        // return  QuartzJobExecution.class;

    }

    /**
     * 构建触发器对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     *
     * @param scheduler
     * @param job
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws Exception {
        // 因为是基于类层面的，直接使用包路径获取类对象
        String invokeTarget = job.getInvokeTarget();
        // 获取到该类的对象，该类继承Job
        Class<? extends Job> aClass = (Class<? extends Job>) Class.forName(invokeTarget);
        // 构建Job信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(aClass).withIdentity(getJobKey(jobId, jobGroup)).storeDurably().build();

        // 构建表达式调度器,添加过期策略
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按照新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取 好像不太建议放入实体类，因为要存储到数据库，如果后期修改了实体类就会存在问题
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES,job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId,jobGroup))) {
            // 防止创建时存在数据问题，先移除，然后再执行操作
            scheduler.deleteJob(getJobKey(jobId,jobGroup));
        }

        // 启动这个定时任务
        scheduler.scheduleJob(jobDetail,trigger);

        // 先暂停任务
        if (job.getStatus().equals(ScheduleConstants.Status.PAUSE.getValue())) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }

    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb)
            throws Exception {
        switch (job.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new Exception("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks");
        }
    }

}
