package com.shw.quartz.service.impl;
import	java.beans.Transient;

import com.shw.quartz.domain.SysJob;
import com.shw.quartz.mapper.JobMapper;
import com.shw.quartz.service.JobService;
import com.shw.quartz.util.ScheduleConstants;
import com.shw.quartz.util.ScheduleUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 14:52
 * @description
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    @Override
    public List<SysJob> selectJobList(SysJob sysJob) {
        return jobMapper.selectJobList(sysJob);
    }

    @Override
    public SysJob getJobById(Long jobId) {
        return jobMapper.getJobById(jobId);

    }

    /**
     * 创建新的定时任务
     * @param sysJob
     * @return
     * @throws Exception
     */
    @Override
    public int insert(SysJob sysJob) throws Exception {
        // 一开始添加为暂停状态
        sysJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.insertJob(sysJob);
        // 如果添加成功了，则创建任务
        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, sysJob);
        }
        return rows;
    }

    /**
     * 任务调度状态修改
     *
     * @param job
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysJob job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            // 恢复定时任务
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            // 暂定定时任务
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 修改定时任务
     * @param job
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJob job) throws Exception {
        SysJob jobById = getJobById(job.getJobId());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            updateSchedulerJob(job,jobById.getJobGroup());
        }
        return rows;
    }

    /**
     * 定时任务立即执行一次
     * @param job
     */
    @Override
    public void run(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob jobById = getJobById(jobId);
        // 参数
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES,jobById);
        // 手动触发任务
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId,jobGroup),jobDataMap);

    }

    /**
     * 批量删除定时任务
     * @param jobIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(Long[] jobIds) throws SchedulerException {
        for (Long jobId : jobIds) {
            SysJob jobById = jobMapper.getJobById(jobId);
            deleteJob(jobById);
        }
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     * @param job
     * @return
     */
    @Transactional
    public int deleteJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = jobMapper.deleteJobById(jobId);
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId,jobGroup));
        }
        return rows;
    }

    public void updateSchedulerJob(SysJob job,String jobGroup) throws Exception {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题，先移除，然后再执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler,job);
    }

    /**
     * 恢复任务
     *
     * @param job
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 暂停任务
     * @param job
     * @return
     * @throws SchedulerException
     */
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

}
