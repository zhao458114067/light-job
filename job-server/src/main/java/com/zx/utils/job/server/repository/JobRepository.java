package com.zx.utils.job.server.repository;

import com.zx.common.repository.BaseRepository;
import com.zx.utils.job.server.entity.JobEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/3/5 11:06
 */
public interface JobRepository extends BaseRepository<JobEntity, Long> {
    /**
     * 查询某个节点之后的任务
     *
     * @param time
     * @param status
     * @param valid
     * @return
     */
    List<JobEntity> queryByNextExecuteTimeBeforeAndStatusAndValid(Long time, Integer status, Integer valid);

    /**
     * 通过job名称与组名称查询
     *
     * @param jobName
     * @param groupName
     * @return
     */
    List<JobEntity> queryByJobNameAndGroupName(String jobName, String groupName);

    /**
     * 通过jobId加排他锁
     *
     * @param id
     * @return
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "from JobEntity je where je.id = :id")
    JobEntity lockJob(@Param("id") Long id);
}
