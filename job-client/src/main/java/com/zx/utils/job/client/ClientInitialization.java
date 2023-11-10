package com.zx.utils.job.client;

import com.zx.common.base.utils.SpringManager;
import com.zx.utils.job.client.annotation.LightJob;
import com.zx.utils.job.client.service.JobRegistryService;
import com.zx.utils.job.common.model.bo.JobDetailBO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhaoXu
 * @date 2022/3/5 17:49
 */
@Component
public class ClientInitialization implements SmartInitializingSingleton {
    @Autowired
    private JobRegistryService jobRegistryService;

    private static final ScheduledExecutorService JOB_REGISTRY_EXECUTOR = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("jobRegister").build());

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext applicationContext = SpringManager.getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        if (ObjectUtils.isNotEmpty(beanDefinitionNames)) {
            // 扫描lightJob注解，向本地注册
            for (String beanDefinitionName : beanDefinitionNames) {
                Object bean = applicationContext.getBean(beanDefinitionName);
                Map<Method, LightJob> method2LightJobMap = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<LightJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, LightJob.class));
                if (ObjectUtils.isNotEmpty(method2LightJobMap)) {
                    method2LightJobMap.forEach((method, lightJob) -> {
                        JobDetailBO jobDetailBO = new JobDetailBO();
                        String jobName = lightJob.value();
                        jobDetailBO.setJobName(jobName);
                        jobDetailBO.setBeanName(beanDefinitionName);
                        jobDetailBO.setTargetMethod(method);
                        jobRegistryService.addJob(jobDetailBO);
                    });
                }
            }

            // 定时向服务端注册
            JOB_REGISTRY_EXECUTOR.scheduleAtFixedRate(() -> {
                jobRegistryService.registryLightJob();
            }, 5, 30, TimeUnit.SECONDS);
        }
    }
}
