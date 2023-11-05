package com.zx.utils.job.client.request;

import com.zx.common.base.model.BaseResponse;
import com.zx.common.rpc.annotation.RequestClient;
import com.zx.utils.job.common.model.bo.JobRegistryBO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ZhaoXu
 * @date 2023/11/4 19:28
 */
@RequestClient("light.job.server.domain")
public interface JobServerService {
    /**
     * 向服务注册job
     *
     * @param jobRegistryBO
     * @return
     */
    @PostMapping("/light-job/job/registry")
    BaseResponse<Void> registryJob(@RequestBody JobRegistryBO jobRegistryBO);
}
