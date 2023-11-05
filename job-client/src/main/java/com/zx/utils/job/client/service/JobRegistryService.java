package com.zx.utils.job.client.service;

import com.zx.utils.job.client.annotation.LightJob;
import com.zx.utils.job.common.model.bo.JobDetailBO;

import java.lang.reflect.Method;

/**
 * @author ZhaoXu
 * @date 2023/11/4 18:12
 */
public interface JobRegistryService {
    /**
     * 添加一个任务
     *
     * @param jobRegistryBO
     * @return
     */
    void addJob(JobDetailBO jobRegistryBO);

    /**
     * 通过名称获取执行方法
     *
     * @param jobName
     * @return
     */
    JobDetailBO getJob(String jobName);

    /**
     * 注册job
     * @param beanDefinitionName
     * @param method
     * @param lightJob
     */
    void registryLightJob(String beanDefinitionName, Method method, LightJob lightJob);
}
