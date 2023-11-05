package com.zx.utils.job.client.controller;

import com.zx.common.base.model.BaseResponse;
import com.zx.utils.job.client.service.JobExecutorService;
import com.zx.utils.job.client.service.JobRegistryService;
import com.zx.utils.job.common.model.dto.JobBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author ZhaoXu
 * @date 2023/11/4 21:47
 */
@RestController
@RequestMapping("/light-job")
public class JobController {
    @Autowired
    private JobExecutorService jobExecutorService;

    /**
     * 激活某个任务
     *
     * @param jobName
     */
    @PostMapping("/activate/{jobName}")
    public void activate(@PathVariable String jobName) {
        jobExecutorService.activate(jobName);
    }

    @GetMapping("/client/beat")
    public void beat() {
    }
}
