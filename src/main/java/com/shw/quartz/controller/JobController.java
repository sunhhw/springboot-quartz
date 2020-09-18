package com.shw.quartz.controller;

import com.shw.quartz.domain.SysJob;
import com.shw.quartz.service.JobService;
import com.shw.quartz.util.AjaxResult;
import com.shw.quartz.util.BaseController;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 14:50
 * @description 任务调度信息处理
 */
@RestController
@RequestMapping("/monitor/job")
public class JobController extends BaseController {

    @Autowired
    private JobService jobService;

    /**
     * 查询定时任务列表
     * @param sysJob
     * @return
     */
    @GetMapping("/list")
    public AjaxResult list(SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        return AjaxResult.success(list);
    }

    /**
     * 获取定时任务的详细信息
     * @return
     */
    @GetMapping("/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
        SysJob job = jobService.getJobById(jobId);
        return AjaxResult.success(job);
    }

    /**
     * 创建一个新的定时任务
     * @param sysJob
     * @return
     * @throws Exception
     */
    @PostMapping()
    public AjaxResult add(@RequestBody SysJob sysJob) throws Exception {
        // 判断cron表达式是否正确
        if (!CronExpression.isValidExpression(sysJob.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        sysJob.setCreateBy("zhangsan");
        return toAjax(jobService.insert(sysJob));
    }

    /**
     * 修改定时任务的状态
     * @param job
     * @return
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob jobById = jobService.getJobById(job.getJobId());
        jobById.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(jobById));
    }

    /**
     * 修改定时任务
     * 在启动的状态下修改定时任务，不会影响代码执行
     * @param job
     * @return
     * @throws Exception
     */
    @PutMapping
    public AjaxResult update(@RequestBody SysJob job) throws Exception {
        if (!CronExpression.isValidExpression(job.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        job.setUpdateBy("lisi");
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务立即执行一次(查看定时任务配置是否正确)
     * @param job
     * @return
     */
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return AjaxResult.success();
    }

    /**
     * 批量删除定时任务
     * @param jobIds
     * @return
     */
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable("jobIds") Long[] jobIds) throws SchedulerException {
        jobService.deleteJobByIds(jobIds);
        return AjaxResult.success();
    }


}
