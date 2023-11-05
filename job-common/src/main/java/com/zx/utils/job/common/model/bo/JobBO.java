package com.zx.utils.job.common.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:33
 */
@Data
public class JobBO implements Serializable {
    private static final long serialVersionUID = -7277210471015365467L;

    private Long id;

    @NotEmpty
    @ApiModelProperty("job名称")
    private String jobName;

    @NotBlank
    @ApiModelProperty("cron表达式")
    private String cronExpression;

    @ApiModelProperty("描述")
    private String jobDesc;

    @ApiModelProperty("状态 0-未启用 1-已启用")
    private Integer status;

    @ApiModelProperty("失败重试次数")
    @Max(3)
    private Integer failRetryCount;
}
