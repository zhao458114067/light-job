package com.zx.utils.job.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.zx.common.base.model.BaseResponse;
import com.zx.common.base.utils.DateUtils;
import com.zx.common.base.utils.HttpClientUtil;
import com.zx.common.base.utils.JsonUtils;
import com.zx.common.base.utils.RetryMonitor;
import com.zx.utils.job.common.constant.Constants;
import com.zx.utils.job.common.model.bo.ActivateJobBO;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.repository.JobRepository;
import com.zx.utils.job.server.service.JobExecutorService;
import com.zx.utils.job.server.service.JobRegistryService;
import com.zx.utils.job.server.util.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ZhaoXu
 * @date 2022/3/5 12:27
 */
@Service
@Slf4j
public class JobExecutorServiceImpl implements JobExecutorService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistryService jobRegistryService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void activateJob(Long jobId) {
        JobEntity jobEntity = jobRepository.lockJob(jobId);
        if (ObjectUtils.isNotEmpty(jobEntity)) {
            Long nextExecuteTime = jobEntity.getNextExecuteTime();
            Long nowTime = DateUtils.getNowTime();
            // 拿到锁前被其它实例执行了
            if (nowTime < nextExecuteTime) {
                return;
            }
            String jobName = jobEntity.getJobName();
            String domain = jobRegistryService.getDomainByJobName(jobName);
            if (ObjectUtils.isNotEmpty(domain)) {
                String params = jobEntity.getParams();
                int partitionCount = Optional.ofNullable(jobEntity.getPartitionCount()).orElse(1);
                // 全部参数列表
                List<Object> paramList = new ArrayList<>();
                if (partitionCount > 1) {
                    try {
                        paramList = JsonUtils.fromJson(params, List.class);
                    } catch (Exception e) {
                        log.error("job指定分片数量时，参数需为List，jobEntity：{}", JsonUtils.toJson(jobEntity));
                    }
                } else {
                    paramList.add(params);
                }
                // 播种注入各个分片的参数列表
                Map<Integer, List<Object>> partitionParamList = new HashMap<>(8);
                for (int i = 0; i < paramList.size(); i++) {
                    Object paramObject = paramList.get(i);
                    Integer partitionIndex = i % partitionCount;
                    partitionParamList.computeIfAbsent(partitionIndex, (k) -> new ArrayList<>()).add(paramObject);
                }
                partitionParamList.values().forEach(item -> {
                    if (ObjectUtils.isNotEmpty(item)) {
                        String parameterString = JsonUtils.toJson(item);
                        boolean needRetry = false;
                        int failRetryCount = Optional.ofNullable(jobEntity.getFailRetryCount()).orElse(0);
                        try {
                            boolean result = activateRemote(jobName, domain, parameterString);
                            needRetry = !result && failRetryCount > 0;
                        } catch (Exception e) {
                            needRetry = failRetryCount > 0;
                            log.error("任务调度发生异常，jobEntity：{}", JsonUtils.toJson(jobEntity), e);
                        }
                        // 支持失败重试
                        if (needRetry) {
                            RetryMonitor.registry(() -> {
                                activateRemote(jobName, domain, parameterString);
                            }, failRetryCount);
                        }
                    }
                });
                try {
                    Long nextExecutionTime = CronUtils.calculateNextExecutionTime(jobEntity.getCronExpression());
                    jobEntity.setNextExecuteTime(nextExecutionTime);
                    jobRepository.save(jobEntity);
                } catch (ParseException e) {
                    log.error("解析cron表达式出错， jobName：{}， CronExpression：{}", jobName, jobEntity.getCronExpression());
                }
            }
        }
    }

    /**
     * 激活远程客户端任务
     *
     * @param jobName
     * @param domain
     * @param params
     * @return
     */
    private boolean activateRemote(String jobName, String domain, String params) {
        ActivateJobBO activateJobBO = new ActivateJobBO();
        activateJobBO.setJobName(jobName);
        activateJobBO.setParams(params);
        BaseResponse<?> post = HttpClientUtil.post(domain + "/light-job/activate", activateJobBO, BaseResponse.class);
        if (Objects.equals(Constants.SUCCESS_CODE, post.getCode())) {
            log.info("任务激活成功，jobName：{}，执行节点：{}", jobName, domain);
            return true;
        } else {
            log.error("任务激活失败, jobName：{}，domain：{}", jobName, domain);
        }
        return false;
    }
}
