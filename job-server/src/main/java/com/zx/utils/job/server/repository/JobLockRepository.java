package com.zx.utils.job.server.repository;

import com.zx.common.repository.BaseRepository;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.entity.JobLockEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

/**
 * @author ZhaoXu
 * @date 2023/11/5 11:06
 */
public interface JobLockRepository extends BaseRepository<JobLockEntity, String> {
    /**
     * 通过jobName加排他锁
     *
     * @param jobName
     * @return
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select jle.jobName from JobLockEntity jle where jle.jobName = :jobName")
    String lockByJobId(@Param("jobName") String jobName);
}
