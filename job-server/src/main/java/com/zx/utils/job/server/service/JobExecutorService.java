package com.zx.utils.job.server.service;

/**
 * @author ZhaoXu
 * @date 2022/3/5 12:26
 */
public interface JobExecutorService {
    /**
     * 激活某个任务
     * @param job
     */
    void activateJob(Long job);
}
