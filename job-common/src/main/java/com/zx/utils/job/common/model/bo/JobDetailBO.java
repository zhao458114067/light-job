package com.zx.utils.job.common.model.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author ZhaoXu
 * @date 2022/3/4 22:00
 */
@Data
public class JobDetailBO implements Serializable {
    private static final long serialVersionUID = 1553441212937620240L;

    /**
     * job名称
     */
    private String jobName;

    /**
     * bean名称
     */
    private String beanName;

    /**
     * 目标方法
     */
    private Method targetMethod;
}
