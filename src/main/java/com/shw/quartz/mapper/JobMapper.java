package com.shw.quartz.mapper;

import com.shw.quartz.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 14:51
 * @description
 */
@Mapper
public interface JobMapper {

    List<SysJob> selectJobList(SysJob sysJob);

    SysJob getJobById(Long jobId);

    int insertJob(SysJob sysJob);

    int updateJob(SysJob job);

    int deleteJobById(Long jobId);
}
