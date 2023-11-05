package com.zx.utils.job.server.service.impl;

import com.zx.common.base.model.BaseResponse;
import com.zx.common.base.utils.DateUtils;
import com.zx.common.base.utils.HttpClientUtil;
import com.zx.utils.job.common.constant.Constants;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.repository.JobLockRepository;
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
import java.util.Optional;

/**
 * @author ZhaoXu
 * @date 2023/11/5 12:27
 */
@Service
@Slf4j
public class JobExecutorServiceImpl implements JobExecutorService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLockRepository jobLockRepository;

    @Autowired
    private JobRegistryService jobRegistryService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean activateJob(JobEntity job) {
        String jobName = job.getJobName();
        String lockKey = jobLockRepository.lockByJobId(jobName);
        Optional<JobEntity> byId = jobRepository.findById(job.getId());
        if (byId.isPresent()) {
            job = byId.get();
            Long nextExecuteTime = job.getNextExecuteTime();
            Long nowTime = DateUtils.getNowTime();
            // 被其它实例执行了
            if (nowTime < nextExecuteTime) {
                return true;
            }
            String domain = jobRegistryService.getDomainByJobName(jobName);
            if (ObjectUtils.isNotEmpty(domain)) {
                BaseResponse post = HttpClientUtil.post(domain + "/light-job/activate/" + jobName, null, BaseResponse.class);
                if (Objects.equals(Constants.SUCCESS_CODE, post.getCode())) {
                    try {
                        Long nextExecutionTime = CronUtils.calculateNextExecutionTime(job.getCronExpression());
                        job.setNextExecuteTime(nextExecutionTime);
                        jobRepository.save(job);
                    } catch (ParseException e) {
                        log.error("解析cron表达式出错， jobName：{}， CronExpression：{}", job.getJobName(), job.getCronExpression());
                        return false;
                    }
                    log.info("job {} 激活成功，执行节点：{}", jobName, domain);
                    return true;
                } else {
                    log.error("任务激活失败, jobName：{}，domain：{}", jobName, domain);
                }
            }
        }
        return false;
    }
}
