package com.zx.utils.job.client.service;

/**
 * @author ZhaoXu
 * @date 2023/11/4 21:48
 */
public interface JobExecutorService {
    /**
     * 激活某个任务
     *
     * @param jobName
     */
    void activate(String jobName);
}
