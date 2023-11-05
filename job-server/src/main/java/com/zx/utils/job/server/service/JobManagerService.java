package com.zx.utils.job.server.service;

import com.zx.utils.job.common.model.bo.JobBO;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:34
 */
public interface JobManagerService {
    /**
     * 保存
     *
     * @param jobBO
     */
    void save(JobBO jobBO);
}
