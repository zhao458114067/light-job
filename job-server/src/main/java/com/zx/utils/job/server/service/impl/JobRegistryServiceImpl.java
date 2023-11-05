package com.zx.utils.job.server.service.impl;

import com.zx.utils.job.common.model.bo.JobRegistryBO;
import com.zx.utils.job.server.entity.JobLockEntity;
import com.zx.utils.job.server.repository.JobLockRepository;
import com.zx.utils.job.server.service.JobRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:34
 */
@Service
@Slf4j
public class JobRegistryServiceImpl implements JobRegistryService {
    @Autowired
    private JobLockRepository jobLockRepository;

    private static final Map<String, List<String>> JOB_NAME_2_DOMAINS = new ConcurrentSkipListMap<>();

    private static final Map<String, AtomicInteger> JOB_NAME_2_LOAD_BALANCE = new ConcurrentSkipListMap<>();

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void registry(JobRegistryBO jobRegistryBO, String domain) {
        String jobName = jobRegistryBO.getJobName();
        JobLockEntity jobLockEntity = new JobLockEntity();
        jobLockEntity.setJobName(jobName);
        jobLockRepository.save(jobLockEntity);
        List<String> domainList = JOB_NAME_2_DOMAINS.computeIfAbsent(jobName, (k) -> new CopyOnWriteArrayList<>());
        if (!domainList.contains(domain)) {
            domainList.add(domain);
        }
    }

    @Override
    public String getDomainByJobName(String jobName) {
        List<String> domainList = JOB_NAME_2_DOMAINS.get(jobName);
        if (ObjectUtils.isEmpty(domainList)) {
            log.warn("任务jobName：{} 可用节点为空！", jobName);
            return null;
        }
        int loadBalanceIndex = JOB_NAME_2_LOAD_BALANCE.computeIfAbsent(jobName, (k) -> new AtomicInteger(0)).incrementAndGet();
        int domainIndex = loadBalanceIndex % domainList.size();
        return domainList.get(domainIndex);
    }

    @Override
    public List<String> getAllDomains() {
        return JOB_NAME_2_DOMAINS.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    @Override
    public void removeDomain(String domain) {
        JOB_NAME_2_DOMAINS.forEach((k, v) -> {
            v.remove(domain);
        });
    }
}
