package com.shw.quartz.service;

import com.shw.quartz.domain.SysJob;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 14:52
 * @description
 */
public interface JobService {

    List<SysJob> selectJobList(SysJob sysJob);

    SysJob getJobById(Long jobId);

    int insert(SysJob sysJob) throws Exception;

    int changeStatus(SysJob jobById) throws SchedulerException;

    int updateJob(SysJob job) throws Exception;

    void run(SysJob job) throws SchedulerException;

    void deleteJobByIds(Long[] jobIds) throws SchedulerException;
}
