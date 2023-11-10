package com.zx.utils.job.server;

import com.zx.common.base.utils.DateUtils;
import com.zx.common.base.utils.HttpClientUtil;
import com.zx.common.base.utils.JsonUtils;
import com.zx.common.base.utils.RetryMonitor;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.repository.JobRepository;
import com.zx.utils.job.server.service.JobExecutorService;
import com.zx.utils.job.server.service.JobRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhaoXu
 * @date 2022/3/4 18:29
 */
@Component
@Slf4j
public class ServerInitialization implements ApplicationRunner {
    @Autowired
    private JobExecutorService jobExecutorService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistryService jobRegistryService;

    private static final ScheduledExecutorService ACTIVATE_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("jobActivator").build());

    private static final ScheduledExecutorService HEART_BEAT_EXECUTOR = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("HeartBeater").build());

    @Override
    public void run(ApplicationArguments args) {
        // 激活任务检查
        activateJobCheck();

        // 节点健康检查
        clientHealthCheck();
    }

    private void clientHealthCheck() {
        HEART_BEAT_EXECUTOR.scheduleAtFixedRate(() -> {
            List<String> allDomains = jobRegistryService.getAllDomains();
            for (String domain : allDomains) {
                try {
                    HttpClientUtil.get(domain + "/light-job/client/beat", Map.class);
                } catch (Exception e) {
                    jobRegistryService.removeDomain(domain);
                    log.warn("服务不可用，已摘除，domain：{}", domain);
                }
            }
        }, 3, 15, TimeUnit.SECONDS);
    }

    private void activateJobCheck() {
        ACTIVATE_TASK_EXECUTOR.scheduleAtFixedRate(() -> {
            Long nowTime = DateUtils.getNowTime();
            List<JobEntity> jobList = jobRepository.queryByNextExecuteTimeBeforeAndStatusAndValid(nowTime, 1, 1);
            if (ObjectUtils.isNotEmpty(jobList)) {
                for (JobEntity jobEntity : jobList) {
                    Long jobId = jobEntity.getId();
                    boolean needRetry = false;
                    Integer failRetryCount = Optional.ofNullable(jobEntity.getFailRetryCount()).orElse(0);
                    try {
                        boolean result = jobExecutorService.activateJob(jobId);
                        needRetry = !result && failRetryCount > 0;
                    } catch (Exception e) {
                        needRetry = failRetryCount > 0;
                        log.error("任务调度发生异常，jobEntity：{}", JsonUtils.toJson(jobEntity), e);
                    }
                    if (needRetry) {
                        RetryMonitor.registry(() -> {
                            jobExecutorService.activateJob(jobId);
                        }, failRetryCount);
                    }
                }
            }
        }, 5, 1, TimeUnit.SECONDS);
    }
}
