package com.zx.utils.job.client.exception;

import com.zx.common.base.exception.BaseException;
import lombok.Data;
import lombok.Getter;

/**
 * @author ZhaoXu
 * @date 2023/11/4 21:50
 */
@Getter
public class JobExecutorException extends BaseException {
    private static final long serialVersionUID = 7662940549047136614L;

    public JobExecutorException(String message) {
        super(message);
    }
}
