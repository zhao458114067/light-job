package com.zx.utils.job.server.service;

import com.zx.utils.job.server.entity.JobEntity;

/**
 * @author ZhaoXu
 * @date 2023/11/5 12:26
 */
public interface JobExecutorService {
    /**
     * 激活某个任务
     * @param job
     */
    boolean activateJob(JobEntity job);
}
