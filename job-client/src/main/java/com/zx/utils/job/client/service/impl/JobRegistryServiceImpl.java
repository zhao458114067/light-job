package com.zx.utils.job.client.service.impl;

import com.zx.common.base.utils.RetryMonitor;
import com.zx.utils.job.client.annotation.LightJob;
import com.zx.utils.job.client.request.JobServerService;
import com.zx.utils.job.client.service.JobRegistryService;
import com.zx.utils.job.common.model.bo.JobDetailBO;
import com.zx.utils.job.common.model.bo.JobRegistryBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhaoXu
 * @date 2022/3/4 18:12
 */
@Service
@Slf4j
public class JobRegistryServiceImpl implements JobRegistryService {
    private static final Map<String, JobDetailBO> JOB_NAME2_METHOD_MAP = new ConcurrentHashMap<>();

    @Autowired
    private JobServerService jobServerService;

    @Value("${server.port}")
    Integer port;

    @Override
    public void addJob(JobDetailBO jobDetailBO) {
        Method targetMethod = jobDetailBO.getTargetMethod();
        String beanName = jobDetailBO.getBeanName();
        String jobName = jobDetailBO.getJobName();
        if (ObjectUtils.isEmpty(jobName)) {
            log.error("job名称无效，jobName：{}， methodName：{}，beanName：{}", jobName, targetMethod.getName(), beanName);
            return;
        }

        JOB_NAME2_METHOD_MAP.put(jobName, jobDetailBO);
    }

    @Override
    public JobDetailBO getJob(String jobName) {
        return JOB_NAME2_METHOD_MAP.get(jobName);
    }

    @Override
    public void registryLightJob(String beanDefinitionName, Method method, LightJob lightJob) {
        String jobName = lightJob.value();
        JobDetailBO jobDetailBO = new JobDetailBO();
        jobDetailBO.setJobName(jobName);
        jobDetailBO.setBeanName(beanDefinitionName);
        jobDetailBO.setTargetMethod(method);
        // 注册本地job路由
        addJob(jobDetailBO);
        JobRegistryBO jobRegistryBO = new JobRegistryBO();
        jobRegistryBO.setJobName(jobName);
        jobRegistryBO.setPort(port);

        try {
            // server注册
            jobServerService.registryJob(jobRegistryBO);
        } catch (Exception e) {
            log.warn("Job 注册异常，请检查，beanDefinitionName：{}，jobName：{}，", beanDefinitionName, jobName);
        }
    }
}
