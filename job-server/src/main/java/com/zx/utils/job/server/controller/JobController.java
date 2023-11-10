package com.zx.utils.job.server.controller;

import com.zx.common.repository.BaseControllerModel;
import com.zx.utils.job.common.model.bo.JobRegistryBO;
import com.zx.utils.job.common.model.bo.JobBO;
import com.zx.utils.job.server.entity.JobEntity;
import com.zx.utils.job.server.service.JobManagerService;
import com.zx.utils.job.server.service.JobRegistryService;
import com.zx.utils.job.server.util.IpAddressUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/3/4 19:41
 */
@RestController
@RequestMapping("/light-job")
public class JobController extends BaseControllerModel<JobBO, JobEntity> {
    @Autowired
    private JobRegistryService jobRegistryService;

    @Autowired
    private JobManagerService jobManagerService;

    @PostMapping("/job/registry")
    public void registry(HttpServletRequest request, @Valid @RequestBody JobRegistryBO jobRegistryBO) {
        String domain = "http://" + IpAddressUtils.getIpAddress(request) + ":" + jobRegistryBO.getPort();
        jobRegistryService.registry(jobRegistryBO, domain);
    }

    @Override
    @PostMapping("/job")
    @ApiOperation(value = "保存", notes = "")
    @Transactional(rollbackFor = {Exception.class})
    public void add(@RequestBody JobBO jobBO) {
        jobManagerService.save(jobBO);
    }
}
