package com.zx.utils.job.common.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ZhaoXu
 * @date 2023/11/4 19:33
 */
@Data
public class JobRegistryBO implements Serializable {
    private static final long serialVersionUID = -7277210471015365467L;

    /**
     * job名称
     */
    @NotEmpty
    private String jobName;

    @NotNull
    private Integer port;
}
