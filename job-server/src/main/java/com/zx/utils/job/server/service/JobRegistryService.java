package com.zx.utils.job.server.service;

import com.zx.utils.job.common.model.bo.JobRegistryBO;

import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:34
 */
public interface JobRegistryService {
    /**
     * 注册
     *
     * @param jobRegistryBO
     * @param domain
     */
    void registry(JobRegistryBO jobRegistryBO, String domain);

    /**
     * 通过任务名获取域名
     *
     * @param jobName
     * @return
     */
    String getDomainByJobName(String jobName);

    /**
     * 获取所有域名
     *
     * @return
     */
    List<String> getAllDomains();

    /**
     * 摘除域名
     *
     * @param domain
     */
    void removeDomain(String domain);
}
