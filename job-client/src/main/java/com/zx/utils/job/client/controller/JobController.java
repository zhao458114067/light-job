package com.zx.utils.job.client.controller;

import com.zx.utils.job.client.service.JobExecutorService;
import com.zx.utils.job.common.model.bo.ActivateJobBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhaoXu
 * @date 2022/3/4 21:47
 */
@RestController
@RequestMapping("/light-job")
public class JobController {
    @Autowired
    private JobExecutorService jobExecutorService;

    /**
     * 激活某个任务
     *
     * @param activateJobBO
     */
    @PostMapping("/activate")
    public void activate(@RequestBody ActivateJobBO activateJobBO) {
        jobExecutorService.activate(activateJobBO);
    }

    @GetMapping("/client/beat")
    public void beat() {
    }
}
