package com.zx.utils.job.client.service;

import com.zx.utils.job.common.model.bo.ActivateJobBO;

/**
 * @author ZhaoXu
 * @date 2022/3/4 21:48
 */
public interface JobExecutorService {
    /**
     * 激活某个任务
     *
     * @param activateJobBO
     */
    void activate(ActivateJobBO activateJobBO);
}
