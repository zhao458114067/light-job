package com.zx.utils.job.server.service.impl;

import com.zx.common.base.model.BaseResponse;
import com.zx.common.base.utils.DateUtils;
import com.zx.common.base.utils.HttpClientUtil;
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
import java.util.Objects;

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
    public boolean activateJob(Long jobId) {
        JobEntity jobEntity = jobRepository.lockJob(jobId);
        if (ObjectUtils.isNotEmpty(jobEntity)) {
            Long nextExecuteTime = jobEntity.getNextExecuteTime();
            Long nowTime = DateUtils.getNowTime();
            // 拿到锁前被其它实例执行了
            if (nowTime < nextExecuteTime) {
                return true;
            }
            String jobName = jobEntity.getJobName();
            String domain = jobRegistryService.getDomainByJobName(jobName);
            if (ObjectUtils.isNotEmpty(domain)) {
                ActivateJobBO activateJobBO = new ActivateJobBO();
                activateJobBO.setJobName(jobName);
                activateJobBO.setParams(jobEntity.getParams());
                BaseResponse post = HttpClientUtil.post(domain + "/light-job/activate", activateJobBO, BaseResponse.class);
                if (Objects.equals(Constants.SUCCESS_CODE, post.getCode())) {
                    try {
                        Long nextExecutionTime = CronUtils.calculateNextExecutionTime(jobEntity.getCronExpression());
                        jobEntity.setNextExecuteTime(nextExecutionTime);
                        jobRepository.save(jobEntity);
                        log.info("任务激活成功，jobName：{}，执行节点：{}", jobName, domain);
                        return true;
                    } catch (ParseException e) {
                        log.error("解析cron表达式出错， jobName：{}， CronExpression：{}", jobName, jobEntity.getCronExpression());
                        return false;
                    }
                } else {
                    log.error("任务激活失败, jobName：{}，domain：{}", jobName, domain);
                }
            }
        }
        return false;
    }
}
