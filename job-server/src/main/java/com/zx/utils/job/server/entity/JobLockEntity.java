package com.zx.utils.job.server.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ZhaoXu
 * @date 2022/3/5 12:15
 */
@Entity
@Table(name = "job_lock")
@Data
public class JobLockEntity {
    @Id
    private String jobName;
}
