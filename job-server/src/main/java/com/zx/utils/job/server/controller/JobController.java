package com.zx.utils.job.server.controller;

import com.zx.common.base.model.BaseResponse;
import com.zx.common.repository.BaseControllerModel;
import com.zx.utils.job.common.model.bo.JobRegistryBO;
import com.zx.utils.job.common.model.dto.JobBO;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.service.JobRegistryService;
import com.zx.utils.job.server.util.IpAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author ZhaoXu
 * @date 2023/11/4 19:41
 */
@RestController
@RequestMapping("/light-job")
public class JobController extends BaseControllerModel<JobBO, JobEntity> {
    @Autowired
    private JobRegistryService jobRegistryService;

    @PostMapping("/job/registry")
    public void registry(HttpServletRequest request, @Valid @RequestBody JobRegistryBO jobRegistryBO) {
        String domain = "http://" + IpAddressUtils.getIpAddress(request) + ":" + jobRegistryBO.getPort();
        jobRegistryService.registry(jobRegistryBO, domain);
    }

    @GetMapping("/server/beat")
    public void beat() {
    }
}
