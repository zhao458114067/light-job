package com.zx.utils.job.server.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author ZhaoXu
 * @date 2022/3/5 11:55
 */
@Entity
@Table(name = "job_info")
@Data
@EntityListeners(AuditingEntityListener.class)
public class JobEntity {
    /**
     * 组名称
     */
    private String groupName;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 描述
     */
    private String jobDesc;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 状态 0-未启用 1-已启用
     */
    private Integer status;

    /**
     * 失败重试次数
     */
    private Integer failRetryCount;

    /**
     * 下次执行时间戳
     */
    private Long nextExecuteTime;

    /**
     * 任务执行参数
     */
    private String params;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtCreate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtModified;

    private Integer valid;
}
