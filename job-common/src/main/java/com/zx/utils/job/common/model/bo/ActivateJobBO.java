package com.zx.utils.job.common.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhaoXu
 * @date 2022/11/10 22:47
 */
@Data
public class ActivateJobBO implements Serializable {
    private static final long serialVersionUID = -8725000004320530998L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 执行参数
     */
    private String params;
}
