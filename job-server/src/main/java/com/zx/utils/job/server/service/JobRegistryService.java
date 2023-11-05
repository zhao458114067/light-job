package com.zx.utils.job.server.service;

import com.zx.utils.job.common.model.bo.JobRegistryBO;

import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:34
 */
public interface JobRegistryService {

    void registry(JobRegistryBO jobRegistryDTO, String domain);

    String getDomainByJobName(String jobName);

    List<String> getAllDomains();

    void removeDomain(String domain);
}
