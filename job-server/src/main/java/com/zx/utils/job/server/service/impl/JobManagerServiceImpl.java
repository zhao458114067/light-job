package com.zx.utils.job.server.service.impl;

import com.zx.common.base.utils.BaseConverter;
import com.zx.common.base.utils.JsonUtils;
import com.zx.utils.job.common.model.bo.JobBO;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.repository.JobRepository;
import com.zx.utils.job.server.service.JobManagerService;
import com.zx.utils.job.server.util.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:34
 */
@Service
@Slf4j
public class JobManagerServiceImpl implements JobManagerService {
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void save(JobBO jobBO) {
        JobEntity jobEntity = BaseConverter.convert(jobBO, JobEntity.class);
        jobEntity.setValid(1);
        try {
            Long nextExecutionTime = CronUtils.calculateNextExecutionTime(jobBO.getCronExpression());
            jobEntity.setNextExecuteTime(nextExecutionTime);
        } catch (ParseException e) {
            log.error("解析cron表达式出错， jobEntity：{}", JsonUtils.toJson(jobEntity));
        }
        jobRepository.save(jobEntity);
    }
}
