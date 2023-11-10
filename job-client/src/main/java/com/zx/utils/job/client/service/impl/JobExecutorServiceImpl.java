package com.zx.utils.job.client.service.impl;

import com.zx.common.base.utils.SpringManager;
import com.zx.utils.job.client.exception.JobExecutorException;
import com.zx.utils.job.client.service.JobExecutorService;
import com.zx.utils.job.client.service.JobRegistryService;
import com.zx.utils.job.common.model.bo.ActivateJobBO;
import com.zx.utils.job.common.model.bo.JobDetailBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author ZhaoXu
 * @date 2022/3/4 21:49
 */
@Service
@Slf4j
public class JobExecutorServiceImpl implements JobExecutorService {
    @Autowired
    private JobRegistryService jobRegistryService;

    @Override
    public void activate(ActivateJobBO activateJobBO) {
        String jobName = activateJobBO.getJobName();
        JobDetailBO jobDetailBO = jobRegistryService.getJob(jobName);
        if (ObjectUtils.isEmpty(jobDetailBO)) {
            throw new JobExecutorException("job不存在");
        }
        String beanName = jobDetailBO.getBeanName();
        Object bean = SpringManager.getBean(beanName);
        Method targetMethod = jobDetailBO.getTargetMethod();
        try {
            Parameter[] parameters = targetMethod.getParameters();
            if (ObjectUtils.isNotEmpty(parameters)) {
                String params = activateJobBO.getParams();
                targetMethod.invoke(bean, params);
            } else {
                targetMethod.invoke(bean);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
