package com.zx.utils.job.common.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:33
 */
@Data
@Accessors(chain = true)
public class JobRegistryBO implements Serializable {
    private static final long serialVersionUID = -7277210471015365467L;

    /**
     * 客户端端口
     */
    @NotNull
    private Integer port;

    /**
     * 组名称
     */
    @NotBlank
    private String groupName;

    @NotEmpty
    private List<String> jobNameList;
}
