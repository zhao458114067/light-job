package com.zx.utils.job.server.util;

import com.zx.common.base.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;

/**
 * @author ZhaoXu
 * @date 2022/3/5 17:07
 */
@Slf4j
public class CronUtils {
    public static Long calculateNextExecutionTime(String cronExpression) throws ParseException {
        Long nowTime = DateUtils.getNowTime();
        return new CronExpression(cronExpression).getNextValidTimeAfter(new Date(nowTime)).getTime();
    }
}
