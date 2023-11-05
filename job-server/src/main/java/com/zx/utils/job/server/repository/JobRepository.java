package com.zx.utils.job.server.repository;

import com.zx.common.repository.BaseRepository;
import com.zx.utils.job.server.entity.JobEntity;

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
}
